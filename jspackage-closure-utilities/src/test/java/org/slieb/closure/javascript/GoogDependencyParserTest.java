package org.slieb.closure.javascript;

import com.google.javascript.jscomp.SourceFile;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static com.google.javascript.jscomp.SourceFile.fromCode;
import static com.google.javascript.jscomp.SourceFile.fromInputStream;
import static java.nio.charset.Charset.defaultCharset;
import static junit.framework.Assert.*;


public class GoogDependencyParserTest {

    GoogDependencyParser<SourceFile> parser;

    @Before
    public void setup() {
        parser = new GoogDependencyParser<>(s -> s);
    }

    @Test
    public void parseBaseFindsNoRequiresOrProvides() throws Throwable {
        try (InputStream inputStream = getClass().getResourceAsStream("/closure-library/closure/goog/base.js")) {
            GoogDependencyNode node = parser.parse(fromInputStream("base.js", inputStream, defaultCharset()));
            assertTrue(node.isBaseFile());
            assertTrue(node.getRequires().isEmpty());
            assertTrue(node.getProvides().isEmpty());
        }
    }

    @Test
    public void parseGoogArrayFindsNoRequiresOrProvides() throws Throwable {
        try (InputStream inputStream = getClass().getResourceAsStream("/closure-library/closure/goog/array/array.js")) {
            GoogDependencyNode node = parser.parse(fromInputStream("base.js", inputStream, defaultCharset()));
            assertFalse(node.isBaseFile());
            assertFalse(node.getRequires().isEmpty());
            assertEquals(1, node.getRequires().size());
            assertTrue(node.getRequires().contains("goog.asserts"));
            assertFalse(node.getProvides().isEmpty());
            assertEquals(2, node.getProvides().size());
            assertTrue(node.getProvides().contains("goog.array"));
            assertTrue(node.getProvides().contains("goog.array.ArrayLike"));
        }
    }


    @Test
    public void parseIfConstructs() throws Throwable {
        String code = "if(COMPILED) { goog.require('x'); } else { goog.require('y'); }";
        GoogDependencyNode node = parser.parse(fromCode("inline", code));
        assertTrue(node.getProvides().isEmpty());
        assertFalse(node.getRequires().isEmpty());
        assertEquals(2, node.getRequires().size());
        assertTrue(node.getRequires().contains("x"));
        assertTrue(node.getRequires().contains("y"));
    }

}