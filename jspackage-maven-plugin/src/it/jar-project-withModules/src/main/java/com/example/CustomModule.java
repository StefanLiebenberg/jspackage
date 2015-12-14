package com.example;

import com.google.inject.multibindings.Multibinder;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.inject.AbstractModule;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import slieb.jspackage.api.OptionsHandler;

import static com.google.javascript.jscomp.CheckEventfulObjectDisposal.DisposalCheckingPolicy.AGGRESSIVE;

public class CustomModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), OptionsHandler.class)
                .addBinding()
                .to(CustomOptionsHandler.class);
    }
}


class CustomOptionsHandler implements OptionsHandler {
    public void handle(CompilerOptions compilerOptions) {
        compilerOptions.setCheckEventfulObjectDisposalPolicy(AGGRESSIVE);
        compilerOptions.assumeStrictThis();
        compilerOptions.setCheckMissingReturn(CheckLevel.ERROR);
        compilerOptions.setRemoveUnusedVariables(CompilerOptions.Reach.ALL);
        compilerOptions.setVariableRenaming(VariableRenamingPolicy.ALL);
        compilerOptions.setInlineConstantVars(true);
        compilerOptions.setInlineFunctions(true);
        compilerOptions.setInlineFunctions(CompilerOptions.Reach.ALL);
        compilerOptions.setCheckDeterminism(true);
        compilerOptions.setBrokenClosureRequiresLevel(CheckLevel.ERROR);
        compilerOptions.setCheckTypes(true);
        compilerOptions.setInferConst(true);
        CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
        CompilationLevel.ADVANCED_OPTIMIZATIONS.setTypeBasedOptimizationOptions(compilerOptions);
    }
}