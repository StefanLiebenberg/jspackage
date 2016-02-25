package org.slieb.jspackage.jsunit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slieb.jspackage.jsunit.internal.CachedTestConfigurator;
import org.slieb.kute.api.Resource;

public class JSUnitSingleTestRunner extends Runner {

    private final CachedTestConfigurator testConfigurator;

    public final Resource.Readable testResource;

    public JSUnitSingleTestRunner(Class<?> testClass) {
        throw new IllegalStateException("not supported yet");
    }

    public JSUnitSingleTestRunner(CachedTestConfigurator configurator,
                                  Resource.Readable testResource) {
        this.testConfigurator = configurator;
        this.testResource = testResource;
    }

    public JSUnitSingleTestRunner(Resource.Provider resourceProvider,
                                  Resource.Readable testResource) {
        this.testResource = testResource;
        this.testConfigurator = new CachedTestConfigurator(null);
    }

    public Description getDescription() {
        String path = testResource.getPath();
        String name = path.replace("_test.js", "").replaceAll("\\.", "_").replaceAll("/", ".");
        return Description.createSuiteDescription(path.replaceAll("\\.js", ""), path);
    }

    @Override
    public void run(RunNotifier notifier) {
        Description description = getDescription();
        try {
            notifier.fireTestStarted(description);
            TestExecutor executor = new TestExecutor(testConfigurator.calculator(), testResource, testConfigurator.getTimeout());
            executor.execute();
            if (!executor.isSuccess()) {
                notifier.fireTestFailure(new Failure(description, new RuntimeException()));
            }
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(description, e));
        } finally {
            notifier.fireTestFinished(description);
        }
    }
}
