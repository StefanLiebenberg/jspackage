package org.slieb.jspackage.jsunit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slieb.closure.dependencies.GoogDependencyNode;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.dependencies.DependencyCalculator;
import org.slieb.runtimes.rhino.EnvJSRuntime;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.Reader;
import java.util.concurrent.TimeoutException;

import static org.slieb.jsunit.JsUnitHelper.*;
import static org.slieb.runtimes.Runtimes.evaluateReader;
import static slieb.kute.resources.ResourcePredicates.*;
import static slieb.kute.resources.Resources.filterResources;


public class JSUnitSingleTestRunner extends Runner {

    public final ResourceProvider<? extends Resource.Readable> resourceProvider;

    public final Resource.Readable testResource;

    public final DependencyCalculator<Resource.Readable, GoogDependencyNode> calculator;

    public final Integer timeoutSeconds;

    public JSUnitSingleTestRunner(Class<?> testClass) {
        throw new IllegalStateException("not supported yet");
    }

    public JSUnitSingleTestRunner(ResourceProvider<? extends Resource.Readable> resourceProvider, Resource.Readable testResource, Integer timeoutSeconds) {
        this.resourceProvider = resourceProvider;
        this.testResource = testResource;
        this.timeoutSeconds = timeoutSeconds;
        this.calculator = GoogResources.getCalculatorCast(resourceProvider);
    }

    public JSUnitSingleTestRunner(String testPath, Integer timeout) {
        this.resourceProvider = filterResources(Kute.getDefaultProvider(),
                all(
                        extensionFilter(".js"),
                        any(
                                extensionFilter("env.rhino.js"),
                                r -> r.getPath().startsWith("jdk/nashorn"),
                                r -> r.getPath().startsWith("com/google/javascript/jscomp")
                        ).negate()
                ));
        this.testResource = this.resourceProvider.getResourceByName(testPath);
        this.calculator = GoogResources.getCalculatorCast(resourceProvider);
        this.timeoutSeconds = timeout;
    }

    public Description getDescription() {
        String path = testResource.getPath();
        String name = path.replace("_test.js", "").replaceAll("\\.", "_").replaceAll("/", ".");
        return Description.createSuiteDescription(name, path);
    }

    @Override
    public void run(RunNotifier notifier) {
        Long start = System.currentTimeMillis();
        Description description = getDescription();
        notifier.fireTestStarted(description);
        try (EnvJSRuntime envJSRuntime = new EnvJSRuntime()) {
            envJSRuntime.initialize();


            for (Resource.Readable readable : calculator.getResourcesFor(testResource)) {
                try (Reader reader = readable.getReader()) {
                    evaluateReader(envJSRuntime, reader, readable.getPath());
                }
            }

            envJSRuntime.doLoad();
            if (!isInitialized(envJSRuntime)) {
                initialize(envJSRuntime);
            }
            while (!isFinished(envJSRuntime)) {
                Long diff = System.currentTimeMillis() - start;
                if (diff > (timeoutSeconds * 1000)) {
                    throw new TimeoutException("Timed out at " + diff + "ms");
                }
                envJSRuntime.doWait(100);
            }

            if (!isSuccess(envJSRuntime)) {
                notifier.fireTestFailure(new Failure(description, new RuntimeException()));
            }

        } catch (Exception ioException) {
            notifier.fireTestFailure(new Failure(description, ioException));
        }
        notifier.fireTestFinished(description);
    }
}

