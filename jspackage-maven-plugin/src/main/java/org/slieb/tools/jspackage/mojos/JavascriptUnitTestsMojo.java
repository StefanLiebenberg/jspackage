package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.jsunit.JsUnitHelper;
import org.slieb.runtimes.Runtimes;
import org.slieb.runtimes.rhino.EnvJSRuntime;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static slieb.kute.resources.ResourcePredicates.extensionFilter;
import static slieb.kute.resources.Resources.filterResources;


@Mojo(name = "unit-tests", defaultPhase = LifecyclePhase.TEST)
public class JavascriptUnitTestsMojo extends AbstractPackageMojo {

    @Parameter(name = "testDirectories")
    protected List<File> testDirectories;

    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Preparing to run tests...");

        getLog().info("adding source resources");
        ResourceProvider<? extends Resource.Readable> sourceResources = getSourceResources();

        if (testDirectories == null || testDirectories.isEmpty()) {
            getLog().warn("No testDirectories specified, skipping unit tests");
            return;
        }
        getLog().info("adding test resources");
        ResourceProvider<? extends Resource.Readable> testResources =
                GoogResources.getResourceProviderForSourceDirectories(testDirectories);

        getLog().info("grouping resources");
        ResourceProvider<? extends Resource.Readable> resources =
                filterResources(new GroupResourceProvider<>(ImmutableList.of(sourceResources, testResources)), extensionFilter(".js"));

        getLog().info("filtering testResources to include only _test.js files");
        ResourceProvider<? extends Resource.Readable> testProvider = filterResources(testResources, extensionFilter("_test.js"));

        getLog().info("creating calculator of grouped resources");
        GoogDependencyCalculator calculator = GoogResources.getCalculatorCast(resources);

        int total = 0, failure = 0;

        for (Resource.Readable testResource : testProvider) {

            getLog().info("Running test " + testResource.getPath());
            total++;
            try (EnvJSRuntime runtime = new EnvJSRuntime()) {
                runtime.initialize();
                for (Resource.Readable loadResource : calculator.getResourcesFor(testResource)) {
                    try (Reader reader = loadResource.getReader()) {
                        Runtimes.evaluateReader(runtime, reader, loadResource.getPath());
                    } catch (IOException io) {
                        throw new RuntimeException(io);
                    }
                }

                runtime.doLoad();

                if (!JsUnitHelper.isInitialized(runtime)) {
                    JsUnitHelper.initialize(runtime);
                }

                while (!JsUnitHelper.isFinished(runtime)) {
                    runtime.doWait(100);
                }

                if (!JsUnitHelper.isSuccess(runtime)) {
                    getLog().error(JsUnitHelper.getReport(runtime));
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
