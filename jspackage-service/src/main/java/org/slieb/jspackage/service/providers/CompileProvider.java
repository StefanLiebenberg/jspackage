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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * /compile/script.min.js
 * /compile/script.sourceMap.js
 */
public class CompileProvider implements ResourceProvider<Resource.Readable> {

    public final String outputLocation;

    public final Set<String> inputNamespaces;

    private final LazyCompile lazyCompile;

    private final BuildProvider buildProvider;

    public CompileProvider(ResourceProvider<? extends Resource.Readable> provider,
                           Set<String> inputNamespaces,
                           String outputLocation) {
        this.inputNamespaces = inputNamespaces;
        this.outputLocation = outputLocation;
        this.buildProvider = new BuildProvider(provider);
        this.lazyCompile = new LazyCompile(this.buildProvider, this.inputNamespaces);
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.of(getCompiledResource());
    }


    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        if (outputLocation.equals(path)) {
            return Optional.of(getCompiledResource());
        }
        return Optional.empty();
    }

    public Resource.Readable getCompiledResource() {
        return new CompileResource(outputLocation, lazyCompile);
    }


}


class CompileResource implements Resource.Readable {

    private final String path;

    private final LazyCompile lazyCompile;

    public CompileResource(String path,
                           LazyCompile lazyCompile) {
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

    private final BuildProvider readables;

    private final Set<String> inputNamespaces;

    public LazyCompile(BuildProvider buildProvider,
                       Set<String> inputNamespaces) {
        this.readables = buildProvider;
        this.inputNamespaces = inputNamespaces;
    }

    private CompilerOptions options() {
        return new CompilerOptions();
    }

    private List<SourceFile> sources() {
        return toSourceFiles(this.readables.getResourcesForNamespaceSet(inputNamespaces).stream());
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