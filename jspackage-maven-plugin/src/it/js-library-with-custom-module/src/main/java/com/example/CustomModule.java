package com.example;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CustomPassExecutionTime;

public class CustomModule extends AbstractModule {

    @Override
    protected void configure() {
        // add javascript parser
    }

    @Provides
    public CompilerOptions compilerOptions() {
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.addCustomPass(CustomPassExecutionTime.BEFORE_CHECKS, new CheckPass());
        return compilerOptions;
    }

}