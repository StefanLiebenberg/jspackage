package org.slieb.closure.gss;

import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.GssParserException;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static slieb.kute.Kute.readResource;


public class URLFunctionCssCompilerPassTest extends AbstractCssTest {

    @Test
    public void testReplacesImageUrlWithUrl_NoImagePathSet() throws GssParserException, IOException {
        CssTree linkTree = tree(getCssResource("/stylesheets/link.gss"));
        new URLFunctionCssCompilerPass(linkTree, new DefaultGssUrlConfiguration(null, null)).runPass();
        assertEquals(readResource(getCssResource("/expected/link-without-image-path.css")),
                     printPretty(linkTree));
    }

    @Test
    public void testReplacesImageUrlWithUrl_ImagePathSet() throws GssParserException, IOException, URISyntaxException {
        CssTree linkTree = tree(getCssResource("/stylesheets/link.gss"));
        new URLFunctionCssCompilerPass(linkTree,
                                       new DefaultGssUrlConfiguration(new URI("/resources/images"), null)).runPass();
        assertEquals(readResource(getCssResource("/expected/link-with-image-path.css")), printPretty(linkTree));
    }

    @Test
    public void testReplacesImageUrlWithUrl_ImagePathSetToCDN() throws GssParserException, IOException,
            URISyntaxException {
        CssTree linkTree = tree(getCssResource("/stylesheets/link.gss"));
        new URLFunctionCssCompilerPass(linkTree,
                                       new DefaultGssUrlConfiguration(
                                               new URI("http://cdn.example.com/resources/images/"), null))
                .runPass();
        assertEquals(readResource(getCssResource("/expected/link-with-cdn-image-path.css")), printPretty(linkTree));
    }
}

