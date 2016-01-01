package org.slieb.closure.gss;

import com.google.common.collect.Sets;
import com.google.common.css.SubstitutionMapProvider;
import com.google.common.css.compiler.ast.GssParserException;
import org.junit.Before;
import org.junit.Test;
import slieb.kute.Kute;
import slieb.kute.api.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static slieb.kute.KuteIO.readResource;


public class CompiledGssProviderTest extends AbstractCssTest {

    private Resource.Provider provider;


    @Before
    public void setupProvider() {
        provider = Kute.providerOf(
                getCssResource("/stylesheets/link.gss"),
                getCssResource("/stylesheets/button.gss"),
                getCssResource("/stylesheets/widget.gss"));
    }


    @Test
    public void testGssCompile() throws IOException, GssParserException {
        Map<String, Set<String>> compileMap = new HashMap<>();
        compileMap.put("/output/style.gss", Sets.newHashSet("widget"));
        CompiledGssProvider compiledGssProvider = new CompiledGssProvider(compileMap, null, provider);
        assertNotNull(compiledGssProvider.getResourceByName("/output/style.gss"));
        assertEquals(
                readResource(getCssResource("/expected/widget.css")),
                printPretty(tree(compiledGssProvider.getResourceByName("/output/style.gss").get())));
    }

    @Test
    public void testGssCompileWithRenameMap() throws IOException, GssParserException {
        Map<String, Set<String>> compileMap = new HashMap<>();
        compileMap.put("/output/style.gss", Sets.newHashSet("widget"));
        Map<String, String> renameMap = new HashMap<>();
        renameMap.put("widget", "w");
        renameMap.put("link", "l");
        renameMap.put("button", "b");
        SubstitutionMapProvider substitutionMapProvider = () -> new ProductionSubstitutionMap(renameMap);
        CompiledGssProvider compiledGssProvider = new CompiledGssProvider(compileMap, substitutionMapProvider,
                provider);
        Resource.Readable style = compiledGssProvider.getResourceByName("/output/style.gss").get();
        assertNotNull(style);
        assertNotEquals(readResource(getCssResource("/expected/widget.css")), printPretty(tree(style)));
        assertEquals(readResource(getCssResource("/expected/widget-renamed.css")), printPretty(tree(style)));
    }

}