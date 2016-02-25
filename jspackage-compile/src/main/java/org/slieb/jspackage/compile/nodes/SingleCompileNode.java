package org.slieb.jspackage.compile.nodes;

import com.google.javascript.jscomp.CompilerOptions;
import org.slieb.kute.api.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

//import org.slieb.kute.api.Resource;

public class SingleCompileNode extends CompileNode {

    @Nonnull
    private final Set<String> requiredNamespaces;

    public SingleCompileNode(@Nonnull final Resource.Provider provider,
                             @Nonnull final Resource.Provider externs,
                             @Nonnull final CompilerOptions options,
                             @Nonnull final Set<String> requires,
                             @Nullable final Resource.Readable jsDefines,
                             @Nullable final Resource.Readable cssRenameMap) {
        super(provider, externs, options, jsDefines, cssRenameMap);
        this.requiredNamespaces = requires;
    }


    @Nonnull
    public Set<String> getRequiredNamespaces() {
        return requiredNamespaces;
    }

}
