package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.javascript.jscomp.CompilerOptions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.slieb.jspackage.compile.CompilerProvider;
import org.slieb.jspackage.compile.Configuration;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static slieb.kute.resources.Resources.copyResource;
import static slieb.kute.resources.Resources.fileResource;


@Mojo(name = "compile",
        defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class CompileJavascriptMojo extends AbstractPackageMojo {

    @Parameter(required = true)
    public List<String> inputs;

    @Parameter(name = "outputFile", required = true)
    public File outputFile;


    @Inject
    @Named("compilerOptions")
    Provider<CompilerOptions> compilerOptionsProvider;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getInjector().injectMembers(this);

        MojoModule module = new MojoModule();
        module.setInputNamespaces(inputs);
        MojoConfiguration configuration = new MojoConfiguration();
        configuration.setCompilerOptions(compilerOptionsProvider.get());
        configuration.setExterns(Kute.providerOf());
        configuration.setModules(Lists.newArrayList(module));
        CompilerProvider compilerProvider = new CompilerProvider(configuration);

        Resource.Readable compiled = compilerProvider.getCompiledResource();

        if (outputFile.getParentFile().exists() || outputFile.getParentFile().mkdirs()) {
            Resource.Writeable outputResource = fileResource(outputFile);
            try {
                copyResource(compiled, outputResource);
            } catch (IOException e) {
                throw new MojoExecutionException("failed", e);
            }
        }

    }
}


class MojoConfiguration implements Configuration {

    private ResourceProvider<? extends Resource.Readable> source, externs;
    private List<Module> modules;
    private CompilerOptions compilerOptions;

    @Override
    public ResourceProvider<? extends Resource.Readable> getSourceProvider() {
        return source;
    }

    @Override
    public ResourceProvider<? extends Resource.Readable> getExternsProvider() {
        return externs;
    }

    @Override
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }


    public void setSource(ResourceProvider<? extends Resource.Readable> source) {
        this.source = source;
    }

    public void setExterns(ResourceProvider<? extends Resource.Readable> externs) {
        this.externs = externs;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void setCompilerOptions(CompilerOptions compilerOptions) {
        this.compilerOptions = compilerOptions;
    }
}

class MojoModule implements Configuration.Module {

    private List<String> inputNamespaces;

    private Resource.Writeable outputResource;

    @Override
    public List<String> getInputNamespaces() {
        return inputNamespaces;
    }

    @Override
    public Resource.Writeable getOutputResource() {
        return outputResource;
    }

    public void setInputNamespaces(List<String> inputNamespaces) {
        this.inputNamespaces = inputNamespaces;
    }

    public void setOutputResource(Resource.Writeable outputResource) {
        this.outputResource = outputResource;
    }
}