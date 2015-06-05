package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.jsunit.TestExecutor;
import org.slieb.jsunit.internal.DefaultTestConfigurator;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import static slieb.kute.resources.ResourcePredicates.extensionFilter;
import static slieb.kute.resources.Resources.filterResources;


@Mojo(name = "unit-tests", defaultPhase = LifecyclePhase.TEST)
public class JavascriptUnitTestsMojo extends AbstractPackageMojo {


    public void execute() throws MojoExecutionException, MojoFailureException {

        info("Preparing to run tests...");
        info("scanning for possible source resources");
        ResourceProvider<? extends Resource.Readable> sourceResources =
                filterResources(getPackageProvider(false),
                        DefaultTestConfigurator.DEFAULT_EXCLUDES);
        info("   found %s possible sources", sourceResources.stream().count());

        info("scanning for possible test resources");
        ResourceProvider<? extends Resource.Readable> testResources = getTestResources();
        info("   found %s possible test resources", testResources.stream().count());

        if (testResources.stream().limit(1).count() == 0) {
            warn("No test sources, skipping unit tests...");
            return;
        }

        info("filtering possible sources and possible test sources to get a list of *.js sources that exclude *_test.js files.");
        ResourceProvider<? extends Resource.Readable> resources =
                filterResources(new GroupResourceProvider<>(ImmutableList.of(testResources, sourceResources)),
                        extensionFilter(".js").and(extensionFilter("_test.js").negate()));
        info("   filtered %s", resources.stream().count());

        info("filtering possible test sources to include only *._test.js files");
        ResourceProvider<? extends Resource.Readable> testProvider = filterResources(testResources, extensionFilter("_test.js"));
        info("   filtered %s", testProvider.stream().count());


        info("creating calculator of grouped resources");
        GoogDependencyCalculator calculator = GoogResources.getCalculatorCast(resources);

        int total = 0, failure = 0;

        for (Resource.Readable testResource : testProvider) {

            info("Running test " + testResource.getPath());
            total++;

            try {
                TestExecutor testExecutor = new TestExecutor(calculator, testResource, 30);
                testExecutor.execute();
                if (!testExecutor.isSuccess()) {
                    error(testExecutor.getReport());
                    failure++;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                failure++;
            }
        }

        info(String.format("%s Tests run with %s failures", total, failure));
        if (failure > 0) {
            throw new MojoFailureException("There were test failures");
        }
    }

}
