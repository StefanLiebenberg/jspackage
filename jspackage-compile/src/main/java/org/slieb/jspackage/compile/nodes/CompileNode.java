package org.slieb.jspackage.compile.nodes;


import com.google.javascript.jscomp.CompilerOptions;
import slieb.kute.api.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Optional;

public abstract class CompileNode implements Serializable {

    @Nonnull
    private final Resource.Provider sourcesProvider;

    @Nonnull
    private final Resource.Provider externsProvider;

    @Nonnull
    private final CompilerOptions compilerOptions;

    @Nullable
    private final Resource.Readable jsDefines;
    
    @Nullable
    private final Resource.Readable cssRenameMap;

    public CompileNode(@Nonnull final Resource.Provider sources,
                       @Nonnull final Resource.Provider externs,
                       @Nonnull final CompilerOptions options,
                       @Nullable final Resource.Readable jsDefines,
                       @Nullable final Resource.Readable cssRenameMap) {
        this.sourcesProvider = sources;
        this.externsProvider = externs;
        this.compilerOptions = options;
        this.jsDefines = jsDefines;
        this.cssRenameMap = cssRenameMap;
    }

    @Nonnull
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    @Nonnull
    public Resource.Provider getExternsProvider() {
        return externsProvider;
    }

    @Nonnull
    public Resource.Provider getSourcesProvider() {
        return sourcesProvider;
    }

    public Optional<Resource.Readable> getJsDefines() {
        return Optional.ofNullable(jsDefines);
    }

    public Optional<Resource.Readable> getCssRenameMap() {
        return Optional.ofNullable(cssRenameMap);
    }
}
