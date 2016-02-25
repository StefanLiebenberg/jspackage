package org.slieb.jspackage.compile.providers;

import org.slieb.jspackage.compile.nodes.ModuleGroupCompileNode;
import org.slieb.jspackage.compile.result.ModuleGroupCompilationResult;
import org.slieb.jspackage.compile.tasks.ModuleCompileTask;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;

import java.util.Optional;
import java.util.stream.Stream;

public class ModuleCompiler implements Resource.Provider {

    private final ModuleCompileTask moduleCompileTask;
    private final ModuleGroupCompileNode moduleGroupCompileNode;

    public ModuleCompiler(ModuleCompileTask moduleCompileTask,
                          ModuleGroupCompileNode moduleGroupCompileNode) {
        this.moduleCompileTask = moduleCompileTask;
        this.moduleGroupCompileNode = moduleGroupCompileNode;
    }

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        return Kute.findResource(stream(), path);
    }

    @Override
    public Stream<Resource.Readable> stream() {
        final ModuleGroupCompilationResult moduleCompilationResult = moduleCompileTask.perform(moduleGroupCompileNode);
        switch (moduleCompilationResult.getType()) {
            case SUCCESS:
                return ((ModuleGroupCompilationResult.Success) moduleCompilationResult).getModuleUnits()
                                                                                       .stream()
                                                                                       .map(ModuleGroupCompilationResult
                                                                                                    .ModuleUnitCompilationResult::getResource);
            case FAILURE:
                return Stream.empty();
            default:
                throw new IllegalStateException("not possible");
        }
    }
}
