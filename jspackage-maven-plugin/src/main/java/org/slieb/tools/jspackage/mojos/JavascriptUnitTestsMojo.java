package org.slieb.tools.jspackage.mojos;

import com.google.javascript.jscomp.SourceFile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.closure.javascript.GoogDependencyHelper;
import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.closure.javascript.GoogDependencyParser;
import org.slieb.dependencies.DependencyCalculator;
import org.slieb.jspackage.runtimes.rhino.EnvJSRuntime;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.implementations.FileResource;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import static com.google.javascript.jscomp.SourceFile.fromReader;
import static java.lang.String.format;
import static org.slieb.closure.javascript.GoogResources.getResourceProviderForSourceDirectories;
import static org.slieb.jspackage.runtimes.JavascriptRuntimeUtils.evaluateReader;
import static slieb.kute.resources.ResourceFilters.extensionFilter;
import static slieb.kute.resources.Resources.filterResources;


@Mojo(name = "unit-tests", defaultPhase = LifecyclePhase.TEST)
public class JavascriptUnitTestsMojo extends AbstractMojo {

    @Parameter(name = "sourceDirectories")
    public Collection<File> sourceDirectories;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (sourceDirectories == null || sourceDirectories.isEmpty()) {
            getLog().warn("sourceDirectories is empty or not specified. Not running any js unit tests.");
            return;
        }

        getLog().info("finding js tests...");

        for (File f : sourceDirectories) {
            getLog().info(" directory: " + f);
        }

        ResourceProvider<FileResource> group = getResourceProviderForSourceDirectories(sourceDirectories, ".js");
        

        GoogDependencyParser<FileResource> parser = new GoogDependencyParser<>(this::convertResource);
        GoogDependencyHelper<FileResource> helper = new GoogDependencyHelper<>();
        DependencyCalculator<FileResource, GoogDependencyNode<FileResource>> calculator =
                new DependencyCalculator<>(group.getResources(), parser, helper);

        ResourceProvider<FileResource> testResources = filterResources(group, extensionFilter(".unit-test.js"));
        for (FileResource test : testResources.getResources()) {
            getLog().info(format("Running test %s", test.getPath()));
            try (EnvJSRuntime envJSRuntime = new EnvJSRuntime()) {
                envJSRuntime.initialize();
                for (FileResource fileResource : calculator.getResourcesFor(test)) {
                    try (Reader reader = fileResource.getReader()) {
                        evaluateReader(envJSRuntime, reader, fileResource.getPath());
                    }
                }
                envJSRuntime.doLoad();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    public SourceFile convertResource(Resource.Readable readable) {
        try (Reader reader = readable.getReader()) {
            return fromReader(readable.getPath(), reader);
        } catch (IOException ioException) {
            throw new RuntimeException("Can't convert to source file", ioException);
        }
    }

}
