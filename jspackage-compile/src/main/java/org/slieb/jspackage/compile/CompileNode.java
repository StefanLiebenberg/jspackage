package org.slieb.jspackage.compile;


import com.google.javascript.jscomp.CompilerOptions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.Serializable;

public class CompileNode implements Serializable {

    private final ResourceProvider<Resource.Readable> sourcesProvider;
    private final ResourceProvider<Resource.Readable> externsProvider;
    private final CompilerOptions compilerOptions;

    public CompileNode(ResourceProvider<Resource.Readable> provider,
                       ResourceProvider<Resource.Readable> externs,
                       CompilerOptions options) {
        this.sourcesProvider = provider;
        this.externsProvider = externs;
        this.compilerOptions = options;

    }


    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    public ResourceProvider<Resource.Readable> getExternsProvider() {
        return externsProvider;
    }

    public ResourceProvider<Resource.Readable> getSourcesProvider() {
        return sourcesProvider;
    }

}
