package org.slieb.tools.jspackage.mojos;

import com.google.common.base.Preconditions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.jsunit.TestExecutor;
import org.slieb.jsunit.internal.DefaultTestConfigurator;
import slieb.kute.Kute;
import slieb.kute.KuteLambdas;
import slieb.kute.api.Resource;

import static slieb.kute.Kute.filterResources;
import static slieb.kute.KuteLambdas.extensionFilter;


@Mojo(name = "unit-tests", defaultPhase = LifecyclePhase.TEST)
public class JSUnitTestsMojo extends AbstractPackageMojo {

    @Parameter(property = "skipTests", defaultValue = "${skipTests}")
    protected Boolean skipTests = false;

    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skipTests) {
            info("skipping tests");
            return;
        }

        info("Preparing to run tests...");
        info("scanning for possible source resources");
        Resource.Provider sourceResources = filterResources(getPackageProvider(false), DefaultTestConfigurator.DEFAULT_EXCLUDES);
        info("   found %s possible sources", sourceResources.stream().count());

        info("scanning for possible test resources");
        Resource.Provider testResources = getTestResources();
        Preconditions.checkNotNull(testResources, "test resources should not be null");
        info("   found %s possible test resources", testResources.stream().count());

        if (testResources.stream().limit(1).count() == 0) {
            warn("No test sources, skipping unit tests...");
            return;
        }

        info("filtering possible sources and possible test sources to get a list of *.js sources that exclude *_test" +
                ".js files.");
        Resource.Provider resources =
                filterResources(Kute.group(testResources, sourceResources),
                        KuteLambdas.all(extensionFilter(".js"), extensionFilter("_test.js").negate()::test));
        info("   filtered %s", resources.stream().count());

        info("filtering possible test sources to include only *._test.js files");
        Resource.Provider testProvider = filterResources(testResources,
                extensionFilter("_test.js"));
        info("   filtered %s", testProvider.stream().count());


        info("creating calculator of grouped resources");
        GoogDependencyCalculator calculator = GoogResources.getCalculator(resources);

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


    public Resource.Provider getTestResources() {
        return Kute.emptyProvider();
    }
}
