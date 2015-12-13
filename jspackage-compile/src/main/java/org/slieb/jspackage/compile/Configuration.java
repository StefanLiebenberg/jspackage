package org.slieb.jspackage.compile;

import com.google.javascript.jscomp.CompilerOptions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.List;

public interface Configuration {

    ResourceProvider<? extends Resource.Readable> getSourceProvider();

    ResourceProvider<? extends Resource.Readable> getExternsProvider();

    List<Module> getModules();

    CompilerOptions getCompilerOptions();

    interface Module {

        List<String> getInputNamespaces();

        Resource.Writable getOutputResource();
    }

}
