package org.slieb.jspackage.compile;


import com.google.javascript.jscomp.CompilerOptions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.Set;

public class ModuleCompileNode extends CompileNode {

    private final Set<SingleModuleCompileNode> modules;

    private final String commonModuleName;


    public ModuleCompileNode(final ResourceProvider<Resource.Readable> sourcesProvider,
                             final ResourceProvider<Resource.Readable> externsProvider,
                             final CompilerOptions options,
                             final Set<SingleModuleCompileNode> modules,
                             final String commonModuleName) {
        super(sourcesProvider, externsProvider, options);
        this.modules = modules;
        this.commonModuleName = commonModuleName;
    }


    public Set<SingleModuleCompileNode> getModuleCompiles() {
        return modules;
    }

    public String getCommonModuleName() {
        return commonModuleName;
    }
}
