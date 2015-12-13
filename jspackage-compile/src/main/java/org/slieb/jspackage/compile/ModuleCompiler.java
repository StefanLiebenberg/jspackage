package org.slieb.jspackage.compile;

import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.Optional;
import java.util.stream.Stream;


public class ModuleCompiler implements ResourceProvider<Resource.Readable> {

    private final ModuleCompileTask moduleCompileTask;
    private final ModuleCompileNode moduleCompileNode;

    public ModuleCompiler(ModuleCompileTask moduleCompileTask,
                          ModuleCompileNode moduleCompileNode) {
        this.moduleCompileTask = moduleCompileTask;
        this.moduleCompileNode = moduleCompileNode;
    }

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        return Kute.findResource(stream(), path);
    }

    @Override
    public Stream<Resource.Readable> stream() {
        final ModuleCompileResult moduleCompileResult = moduleCompileTask.performCompile(moduleCompileNode);
        switch (moduleCompileResult.getType()) {
            case SUCCESS:
                return ((ModuleCompileResult.Success) moduleCompileResult).getOutputResources().stream();
            case FAILURE:
                return Stream.empty();
            default:
                throw new IllegalStateException("not possible");
        }
    }


}
