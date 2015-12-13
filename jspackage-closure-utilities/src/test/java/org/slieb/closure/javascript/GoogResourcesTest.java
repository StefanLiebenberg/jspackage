package org.slieb.closure.javascript;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.SourceFile;
import org.junit.Before;
import org.junit.Test;
import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static slieb.kute.Kute.stringResource;

public class GoogResourcesTest {

    Resource.Readable readableA, readableB, readableC;

    ResourceProvider<Resource.Readable> readables;

    @Before
    public void setup() {
        readableA = stringResource("/path/a", "goog.base = function () {};");
        readableB = stringResource("/path/b", "goog.provide('b');");
        readableC = stringResource("/path/c", "goog.provide('c');");
        readables = Kute.providerOf(readableA, readableB, readableC);
    }

    @Test
    public void testGetCalculator() throws Exception {
        GoogDependencyCalculator calculator = GoogResources.getCalculator(readables);
        assertNotNull(calculator);
        assertEquals(ImmutableList.of(readableA, readableC), calculator.getResourcesFor(readableC));
        assertEquals(ImmutableList.of(readableA, readableB), calculator.getResourcesFor(readableB));
    }


    @Test(expected = RuntimeException.class)

    public void testCalculatorProducesRuntimeError() throws Exception {

        Resource.Readable readable = new Resource.Readable() {
            @Override
            public Reader getReader() throws IOException {
                throw new IOException("fake io");
            }

            @Override
            public String getPath() {
                return "/path";
            }
        };

        GoogResources.getCalculator(Kute.providerOf(readable)).getDependencyNodes();
    }

    @Test
    public void testGetSourceFileFromResource() throws Exception {
        String content = "var x = y;", path = "/path.js";
        Resource.Readable resource = stringResource(path, content);
        SourceFile sourceFile = GoogResources.getSourceFileFromResource(resource);
        assertNotNull(sourceFile);
        assertEquals(content, sourceFile.getCode());
        assertEquals(path, sourceFile.getOriginalPath());

    }
}