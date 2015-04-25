package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.closure.javascript.GoogDependencyHelper;
import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.closure.javascript.GoogDependencyParser;
import org.slieb.closure.javascript.GoogResources;
import org.slieb.dependencies.DependencyCalculator;
import org.slieb.jspackage.jsunit.JSUnitHelper;
import org.slieb.jspackage.runtimes.JavascriptRuntimeUtils;
import org.slieb.jspackage.runtimes.rhino.EnvJSRuntime;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static org.slieb.closure.javascript.GoogResources.getResourceProviderForSourceDirectories;
import static slieb.kute.resources.ResourceFilters.extensionFilter;
import static slieb.kute.resources.Resources.filterResources;


@Mojo(name = "unit-tests", defaultPhase = LifecyclePhase.TEST)
public class JavascriptUnitTestsMojo extends AbstractPackageMojo {

    @Parameter(name = "testDirectories")
    protected List<File> testDirectories;


    public <R extends Resource.Readable> DependencyCalculator<R, GoogDependencyNode<R>> getCalculator(ResourceProvider<? extends R> resourceProvider) {
        GoogDependencyHelper<R> helper = new GoogDependencyHelper<>();
        GoogDependencyParser<R> parser = new GoogDependencyParser<>(GoogResources::getSourceFileFromResource);
        List<R> list = Resources.resourceProviderToList(resourceProvider);
        return new DependencyCalculator<>(list, parser, helper);
    }


    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Preparing to run tests...");

        getLog().info("adding source resources");
        ResourceProvider<? extends Resource.Readable> sourceResources = getSourceResources();

        if (testDirectories == null || testDirectories.isEmpty()) {
            getLog().warn("No testDirectories specified, skipping unit tests");
            return;
        }
        getLog().info("adding test resources");
        ResourceProvider<? extends Resource.Readable> testResources = getResourceProviderForSourceDirectories(testDirectories);

        getLog().info("grouping resources");
        ResourceProvider<? extends Resource.Readable> resources =
                filterResources(new GroupResourceProvider<>(ImmutableList.of(sourceResources, testResources)), extensionFilter(".js"));

        getLog().info("filtering testResources to include only _test.js files");
        ResourceProvider<? extends Resource.Readable> testProvider = filterResources(testResources, extensionFilter("_test.js"));

        getLog().info("creating calculator of grouped resources");
        DependencyCalculator<Resource.Readable, GoogDependencyNode<Resource.Readable>> calculator = getCalculator(resources);

        int total = 0, failure = 0;

        for (Resource.Readable testResource : testProvider) {

            getLog().info("Running test " + testResource.getPath());
            total++;
            try (EnvJSRuntime runtime = new EnvJSRuntime()) {
                runtime.initialize();
                for (Resource.Readable loadResource : calculator.getResourcesFor(testResource)) {
                    try (Reader reader = loadResource.getReader()) {
                        JavascriptRuntimeUtils.evaluateReader(runtime, reader, loadResource.getPath());
                    } catch (IOException io) {
                        throw new RuntimeException(io);
                    }
                }

                runtime.doLoad();

                if (!JSUnitHelper.isInitialized(runtime)) {
                    JSUnitHelper.initialize(runtime);
                }

                while (!JSUnitHelper.isFinished(runtime)) {
                    runtime.doWait(100);
                }

                if (!JSUnitHelper.isSuccess(runtime)) {
                    getLog().error(JSUnitHelper.getReport(runtime));
                    throw new RuntimeException();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
                failure++;
            }
        }

        getLog().info(String.format("%s Tests run with %s failures", total, failure));
        if (failure > 0) {
            throw new MojoFailureException("There were test failures");
        }
    }

}
