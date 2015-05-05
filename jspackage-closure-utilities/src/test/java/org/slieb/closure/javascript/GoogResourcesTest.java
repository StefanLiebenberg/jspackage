package org.slieb.closure.javascript;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.SourceFile;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slieb.dependencies.DependencyCalculator;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.io.IOException;
import java.io.Reader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.slieb.closure.javascript.GoogResources.getCalculator;
import static slieb.kute.resources.Resources.providerOf;

public class GoogResourcesTest {

    Resource.Readable readableA, readableB, readableC;

    ResourceProvider<Resource.Readable> readables;

    @Before
    public void setup() {
        readableA = Resources.stringResource("goog.base = function () {};", "/path/a");
        readableB = Resources.stringResource("goog.provide('b');", "/path/b");
        readableC = Resources.stringResource("goog.provide('c');", "/path/c");
        readables = providerOf(readableA, readableB, readableC);
    }

    @Test
    public void testGetCalculator() throws Exception {
        DependencyCalculator<Resource.Readable, GoogDependencyNode<Resource.Readable>> calculator =
                getCalculator(readables);
        assertNotNull(calculator);
        Assert.assertEquals(ImmutableList.of(readableA, readableC),
                calculator.getResourcesFor(readableC));
        Assert.assertEquals(ImmutableList.of(readableA, readableB),
                calculator.getResourcesFor(readableB));
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

        getCalculator(providerOf(readable)).getDependencyNodes();
    }

    @Test
    public void testGetSourceFileFromResource() throws Exception {
        String content = "var x = y;", path = "/path.js";
        Resource.Readable resource = Resources.stringResource(content, path);
        SourceFile sourceFile = GoogResources.getSourceFileFromResource(resource);
        assertNotNull(sourceFile);
        assertEquals(content, sourceFile.getCode());
        assertEquals(path, sourceFile.getOriginalPath());

    }
}