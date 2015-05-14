package org.slieb.jspackage.service.providers;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CompileProvider implements ResourceProvider<Resource.Readable> {

    private final ToolsProvider toolsProvider;

    private final Configuration configuration;

    private final LazyCompile lazyCompile;


    public CompileProvider(ToolsProvider toolsProvider, Configuration configuration) {
        this.lazyCompile = new LazyCompile(toolsProvider, configuration);
        this.toolsProvider = toolsProvider;
        this.configuration = configuration;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.of(getCompiledResource());
    }

    private Resource.Readable getCompiledResource() {
        return new CompileResource(configuration.outputLocation(), lazyCompile);
    }


    @Override
    public Resource.Readable getResourceByName(String path) {
        if (configuration.outputLocation().equals(path)) {
            return getCompiledResource();
        }
        return null;
    }

    public interface Configuration {

        String outputLocation();

        Set<String> inputNamespaces();
    }

}


class CompileResource implements Resource.Readable {

    private final String path;

    private final LazyCompile lazyCompile;

    public CompileResource(String path, LazyCompile lazyCompile) {
        this.path = path;
        this.lazyCompile = lazyCompile;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(lazyCompile.getCompileOutput());
    }
}


class LazyCompile {

    private final ToolsProvider readables;

    private final CompileProvider.Configuration configuration;

    public LazyCompile(ToolsProvider toolsProvider, CompileProvider.Configuration configuration) {
        this.readables = toolsProvider;
        this.configuration = configuration;
    }

    private CompilerOptions options() {
        CompilerOptions options = new CompilerOptions();
        return options;
    }

    private List<SourceFile> sources() {
        return toSourceFiles(this.readables.getResourcesForNamespaceSet(configuration.inputNamespaces()).stream());
    }


    private List<SourceFile> externs() {
        return toSourceFiles(this.readables.getExterns().stream());
    }

    private List<SourceFile> toSourceFiles(Stream<? extends Resource.Readable> resourceStream) {
        return resourceStream.map(this::toSourceFile).collect(Collectors.toList());
    }

    private SourceFile toSourceFile(Resource.Readable readable) {
        try (Reader reader = readable.getReader()) {
            return SourceFile.fromReader(readable.getPath(), reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String getCompileOutput() {
        Compiler compiler = new Compiler();
        compiler.compile(externs(), sources(), options());
        return compiler.toSource();
    }
}