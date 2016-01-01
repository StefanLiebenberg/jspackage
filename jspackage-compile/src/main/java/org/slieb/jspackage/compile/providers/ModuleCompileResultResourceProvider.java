package org.slieb.jspackage.compile.providers;


import org.slieb.jspackage.compile.result.ModuleGroupCompilationResult;
import slieb.kute.api.Resource;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModuleCompileResultResourceProvider implements Resource.Provider {

    final Supplier<ModuleGroupCompilationResult> resultSupplier;

    public ModuleCompileResultResourceProvider(Supplier<ModuleGroupCompilationResult> resultSupplier) {
        this.resultSupplier = resultSupplier;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        ModuleGroupCompilationResult result = resultSupplier.get();
        switch (result.getType()) {
            case SUCCESS:
                return new ModuleSuccessResultResourceProvider((ModuleGroupCompilationResult.Success) result).stream();
            default:
                return Stream.empty();
        }
    }
    
}
