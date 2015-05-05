package org.slieb.tools.jspackage.internal;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.javascript.jscomp.CompilerOptions;

import java.util.Set;

public class DefaultsModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), OptionsHandler.class);
    }

    @Provides
    @Named("compilerOptions")
    public CompilerOptions compilerOptions(CompilerOptions options, Set<OptionsHandler> optionHandlers) {
        optionHandlers.forEach(handler -> handler.handle(options));
        return options;
    }

}
