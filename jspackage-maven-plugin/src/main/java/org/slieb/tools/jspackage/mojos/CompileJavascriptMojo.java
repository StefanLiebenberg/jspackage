package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.closure.javascript.GoogDependencyNode;
import slieb.kute.api.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.slieb.closure.javascript.GoogResources.getCalculator;
import static slieb.kute.resources.ResourceFilters.extensionFilter;
import static slieb.kute.resources.Resources.*;


@Mojo(name = "compile", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class CompileJavascriptMojo extends AbstractPackageMojo {

    @Parameter(required = true)
    public List<String> inputs;

    @Parameter(name = "outputFile", required = true)
    public File outputFile;

    public List<SourceFile> getExterns() {
        return ImmutableList.of();
    }

    public List<SourceFile> getInputSourceFiles() {
        return
                getCalculator(filterResources(getSourceResources(), extensionFilter(".js")))
                        .getDependencyResolver()
                        .resolveNamespaces(inputs)
                        .resolve()
                        .stream()
                        .map(GoogDependencyNode::getResource)
                        .map(resource -> {
                            try {
                                return SourceFile.fromReader(resource.getPath(), resource.getReader());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).collect(Collectors.toList());
    }


    @Inject
    Provider<Compiler> compilerProvider;

    @Inject
    @Named("compilerOptions")
    Provider<CompilerOptions> compilerOptionsProvider;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getInjector().injectMembers(this);
        Compiler compiler = compilerProvider.get();
        List<SourceFile> externs = getExterns();
        List<SourceFile> inputs = getInputSourceFiles();
        CompilerOptions options = compilerOptionsProvider.get();
        Result result = compiler.compile(externs, inputs, options);

        if (result.errors.length > 0) {
            throw new MojoExecutionException("There was compile errors");
        }

        if (outputFile.getParentFile().exists() || outputFile.getParentFile().mkdirs()) {
            Resource.Writeable outputResource = fileResource(outputFile);
            try {
                writeResource(outputResource, compiler.toSource());
            } catch (IOException e) {
                throw new MojoExecutionException("failed", e);
            }
        }

    }
}