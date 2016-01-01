package org.slieb.jspackage.compile.legacy;

import com.google.javascript.jscomp.CompilerOptions;
import slieb.kute.api.Resource;

import java.util.List;

public interface Configuration {

    Resource.Provider getSourceProvider();

    Resource.Provider getExternsProvider();

    List<Module> getModules();

    CompilerOptions getCompilerOptions();

    interface Module {

        List<String> getInputNamespaces();

        Resource.Writable getOutputResource();
    }

}
