package org.slieb.jspackage.compile;


import com.google.common.base.Preconditions;
import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogDependencyNode;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.dependencies.DependencyResolver;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

public class CompilerProvider implements ResourceProvider<Resource.Readable> {

    private final Configuration configuration;

    private Pair<Compiler, Result> cache;

    public CompilerProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    private List<SourceFile> mapToSourceFiles(Stream<? extends Resource.Readable> nodes) {
        return nodes.map(GoogResources::getSourceFileFromResource).collect(toList());
    }

    private List<SourceFile> getExterns() {
        return mapToSourceFiles(configuration.getExternsProvider().stream());
    }

    private List<SourceFile> getInputs() {
        Preconditions.checkNotNull(configuration.getSourceProvider(), "no source provider");
        Preconditions.checkState(configuration.getSourceProvider().stream().count() > 0, "no inputs");

        GoogDependencyCalculator calculator = GoogResources.getCalculator(
                Kute.asReadableProvider(configuration.getSourceProvider()));
        DependencyResolver<GoogDependencyNode> resolver = calculator.getDependencyResolver();
        for (Configuration.Module module : configuration.getModules()) {
            resolver.resolveNamespaces(module.getInputNamespaces());
        }
        return mapToSourceFiles(resolver.resolve().stream().map(GoogDependencyNode::getResource));
    }

    public Pair<Compiler, Result> compile() {
        try {
            if (cache == null) {
                final Compiler compiler = new Compiler();
                final CompilerOptions options = configuration.getCompilerOptions();
//            final List<SourceFile> externs = getExterns();
                final List<SourceFile> externs = AbstractCommandLineRunner.getBuiltinExterns(options);
                final List<SourceFile> inputs = getInputs();
                final Result result = compiler.compile(externs, inputs, options);
                cache = new ImmutablePair<>(compiler, result);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return cache;
    }

    public void clear() {
        cache = null;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.of(getCompiledResource(), getCompileErrorsResource());
    }


    public Resource.Readable getCompiledResource() {
        return new CompiledResource("/compile",
                                    checkNotNull(checkNotNull(compile(), "Compile returns nada").getLeft(),
                                                 "no compile result"));
    }

    public Resource.Readable getCompileErrorsResource() {
        return new ResultErrorsResource("/errors", compile().getRight());
    }

    public Optional<Resource.Readable> getResourceByName(String path) {
        return Kute.findResource(stream(), path);
    }

}

class CompiledResource implements Resource.Readable {

    private final String path;
    private final Compiler compiler;

    public CompiledResource(String path,
                            Compiler compiler) {
        this.path = path;
        this.compiler = compiler;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(compiler.toSource());
    }

    @Override
    public String getPath() {
        return path;
    }
}

class ResultErrorsResource implements Resource.Readable {
    private final String path;
    private final Result result;

    public ResultErrorsResource(String path,
                                Result result) {
        this.path = path;
        this.result = result;
    }

    @Override
    public Reader getReader() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (JSError error : result.errors) {
            builder.append(error.toString()).append("\n");
        }
        return new StringReader(builder.toString());
    }

    @Override
    public String getPath() {
        return path;
    }
}



