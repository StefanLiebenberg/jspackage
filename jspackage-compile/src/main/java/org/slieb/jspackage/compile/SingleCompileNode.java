package org.slieb.jspackage.compile;


import com.google.javascript.jscomp.CompilerOptions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.Set;

public class SingleCompileNode extends CompileNode {

    private final Set<String> inputNamespaces;

    public SingleCompileNode(final ResourceProvider<Resource.Readable> provider,
                             final ResourceProvider<Resource.Readable> externs,
                             final CompilerOptions options,
                             final Set<String> inputNamespaces) {
        super(provider, externs, options);
        this.inputNamespaces = inputNamespaces;
    }


    public Set<String> getInputNamespaces() {
        return inputNamespaces;
    }

}
