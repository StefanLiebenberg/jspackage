package org.slieb.jspackage.compile.providers;

import org.slieb.jspackage.compile.result.ModuleGroupCompilationResult;
import org.slieb.jspackage.compile.result.ModuleGroupCompilationResult.ModuleUnitCompilationResult;
import slieb.kute.api.Resource;

import java.util.stream.Stream;


public class ModuleSuccessResultResourceProvider implements Resource.Provider {
    private final ModuleGroupCompilationResult.Success success;

    public ModuleSuccessResultResourceProvider(ModuleGroupCompilationResult.Success success) {
        this.success = success;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.concat(
                Stream.of(success.getSourceMapResource()),
                success.getModuleUnits().stream().map(ModuleUnitCompilationResult::getResource));
    }

}
