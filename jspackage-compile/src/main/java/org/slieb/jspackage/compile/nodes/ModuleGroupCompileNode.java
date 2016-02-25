package org.slieb.jspackage.compile.nodes;

import com.google.javascript.jscomp.CompilerOptions;
import org.slieb.kute.api.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ModuleGroupCompileNode extends CompileNode {

    @Nonnull
    private final Set<SingleModuleCompileNode> modules;

    @Nonnull
    private final String commonModuleName;

    public ModuleGroupCompileNode(@Nonnull final Resource.Provider sourcesProvider,
                                  @Nonnull final Resource.Provider externsProvider,
                                  @Nonnull final CompilerOptions options,
                                  @Nonnull final Set<SingleModuleCompileNode> modules,
                                  @Nonnull final String commonModuleName,
                                  @Nullable Resource.Readable jsDefines,
                                  @Nullable Resource.Readable cssRenameMap) {
        super(sourcesProvider, externsProvider, options, jsDefines, cssRenameMap);
        this.modules = modules;
        this.commonModuleName = commonModuleName;
    }

    @Nonnull
    public Set<SingleModuleCompileNode> getModuleCompiles() {
        return modules;
    }

    @Nonnull
    public String getCommonModuleName() {
        return commonModuleName;
    }
}
