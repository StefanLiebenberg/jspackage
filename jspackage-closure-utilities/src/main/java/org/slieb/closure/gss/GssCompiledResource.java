package org.slieb.closure.gss;


import com.google.common.css.JobDescription;
import com.google.common.css.JobDescriptionBuilder;
import com.google.common.css.SourceCode;
import com.google.common.css.compiler.ast.*;
import com.google.common.css.compiler.passes.CompactPrinter;
import com.google.common.css.compiler.passes.PassRunner;
import org.slieb.kute.KuteIO;
import org.slieb.kute.api.Resource;

import java.io.*;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;


public class GssCompiledResource implements Resource.Readable {

    public static final Function<Readable, SourceCode>
            READABLE_TO_SOURCE_CODE = castFunctionWithThrowable(r -> new SourceCode(r.getPath(), KuteIO.readResource(r)));

    private final String path;

    private final GssDependencyProvider gssSources;

    private final JobDescription jobDescription;

    private Function<CssTree, CssCompilerPass> preProccessPass, postProccessPass;

    private final ErrorManager errorManager = new GssErrorManager();

    public GssCompiledResource(String path,
                               GssDependencyProvider gssSources) {
        this(path, gssSources, null, null, null);
    }

    public GssCompiledResource(String path,
                               GssDependencyProvider gssSources,
                               Function<CssTree, CssCompilerPass> preProccessPass,
                               Function<CssTree, CssCompilerPass> postProccessPass,
                               JobDescription jobDescription) {
        this.path = path;
        this.gssSources = gssSources;
        this.jobDescription = jobDescription;
        this.preProccessPass = preProccessPass;
        this.postProccessPass = postProccessPass;
    }


    protected List<SourceCode> getSourceCodeToCompile() {
        return gssSources.stream().map(READABLE_TO_SOURCE_CODE).collect(toList());
    }

    protected CssTree getCssTree() throws GssParserException {
        return new GssParser(getSourceCodeToCompile()).parse();
    }

    protected String printCssTree(CssTree cssTree) {
        final CompactPrinter compactPrinterPass = new CompactPrinter(cssTree);
        compactPrinterPass.runPass();
        return compactPrinterPass.getCompactPrintedString();
    }

    protected void preProccess(CssTree cssTree) {

        if (preProccessPass != null) {
            preProccessPass.apply(cssTree).runPass();
        }
    }

    protected void postProccess(CssTree cssTree) {
        if (postProccessPass != null) {
            postProccessPass.apply(cssTree).runPass();
        }
    }

    protected void compile(CssTree cssTree) {
        final JobDescriptionBuilder jobBuilder = new JobDescriptionBuilder().copyFrom(jobDescription);
        final PassRunner passRunner = new PassRunner(jobBuilder.getJobDescription(), errorManager);
        passRunner.runPasses(cssTree);
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(getContent());
    }

    private String getContent() {
        try {
            final CssTree tree = getCssTree();
            preProccess(tree);
            compile(tree);
            postProccess(tree);
            return printCssTree(tree);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(getContent().getBytes());
    }
}


