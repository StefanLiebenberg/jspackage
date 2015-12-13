package org.slieb.closure.gss;

import com.google.common.css.compiler.ast.CssTree;
import com.google.common.css.compiler.ast.GssParser;
import com.google.common.css.compiler.ast.GssParserException;
import com.google.common.css.compiler.passes.CompactPrinter;
import com.google.common.css.compiler.passes.PrettyPrinter;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.slieb.closure.gss.GssCompiledResource.READABLE_TO_SOURCE_CODE;
import static slieb.kute.Kute.inputStreamResource;


public abstract class AbstractCssTest {

    public Resource.Readable getResource(String path) {
        return inputStreamResource(path,
                                   () -> Optional.ofNullable(getClass().getResourceAsStream(path))
                                           .orElseThrow(() -> new RuntimeException("No input stream for " + path)));
    }

    public Resource.Readable getCssResource(String path) {
        return getResource("/org/slieb/closure/gss" + path);
    }

    public CssTree tree(ResourceProvider<Resource.Readable> provider) throws GssParserException {
        return new GssParser(provider.stream().map(READABLE_TO_SOURCE_CODE).collect(toList())).parse();
    }

    public CssTree tree(Resource.Readable readable) throws GssParserException {
        return new GssParser(READABLE_TO_SOURCE_CODE.apply(readable)).parse();
    }

    public String printPretty(CssTree tree) {
        final PrettyPrinter pretty = new PrettyPrinter(tree.getVisitController());
        pretty.setPreserveComments(true);
        pretty.setStripQuotes(true);
        pretty.runPass();
        return pretty.getPrettyPrintedString();
    }

    public String printCompact(CssTree tree) {
        final CompactPrinter compactPrinterPass = new CompactPrinter(tree);
        compactPrinterPass.runPass();
        return compactPrinterPass.getCompactPrintedString();
    }

}
