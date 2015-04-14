package org.slieb.jspackage.jsunit;

import com.google.common.collect.ImmutableList;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slieb.jspackage.runtimes.rhino.EnvJSRuntime;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.slieb.jspackage.jsunit.JSUnitHelper.*;
import static org.slieb.jspackage.runtimes.JavascriptRuntimeUtils.evaluateResource;


public class JSUnitSingleTestRunner extends Runner {

    public final String path;

    public final String[] sources;

    public final Integer timeoutSeconds;

    public JSUnitSingleTestRunner(Class<?> testClass) {
        throw new IllegalStateException("not supported yet");
    }

    public JSUnitSingleTestRunner(String path, String[] sources, Integer timeoutSeconds) {
        this.path = path;
        this.sources = sources;
        this.timeoutSeconds = timeoutSeconds;
    }


    public Description getDescription() {
        String name = path.replace("_test.js", "").replaceAll("\\.", "_").replaceAll("/", ".");
        return Description.createSuiteDescription(name, path);

    }

    public List<String> getRequiredResourcePaths() {
        return ImmutableList.<String>builder()
                .add("closure-library/closure/goog/base.js")
                .add("closure-library/closure/goog/dom/tagname.js")
                .add("closure-library/closure/goog/promise/thenable.js")
                .add("closure-library/closure/goog/dom/nodetype.js")
                .add("closure-library/closure/goog/debug/error.js")
                .add("closure-library/closure/goog/string/string.js")
                .add("closure-library/closure/goog/asserts/asserts.js")
                .add("closure-library/closure/goog/async/freelist.js")
                .add("closure-library/closure/goog/async/workqueue.js")
                .add("closure-library/closure/goog/debug/entrypointregistry.js")
                .add("closure-library/closure/goog/functions/functions.js")
                .add("closure-library/closure/goog/array/array.js")
                .add("closure-library/closure/goog/labs/useragent/util.js")
                .add("closure-library/closure/goog/object/object.js")
                .add("closure-library/closure/goog/labs/useragent/browser.js")
                .add("closure-library/closure/goog/labs/useragent/engine.js")
                .add("closure-library/closure/goog/async/nexttick.js")
                .add("closure-library/closure/goog/testing/watchers.js")
                .add("closure-library/closure/goog/async/run.js")
                .add("closure-library/closure/goog/promise/resolver.js")
                .add("closure-library/closure/goog/promise/promise.js")
                .add("closure-library/closure/goog/testing/stacktrace.js")
                .add("closure-library/closure/goog/testing/asserts.js")
                .add("closure-library/closure/goog/testing/testcase.js")
                .add("closure-library/closure/goog/testing/testrunner.js")
                .add("closure-library/closure/goog/testing/jsunit.js")
                .build();
    }

    @Override
    public void run(RunNotifier notifier) {
        final ClassLoader classLoader = getClass().getClassLoader();
        Long start = System.currentTimeMillis();
        Description description = getDescription();
        notifier.fireTestStarted(description);
        try (EnvJSRuntime envJSRuntime = new EnvJSRuntime()) {
            envJSRuntime.initialize();
            for (String p : getRequiredResourcePaths()) {
                evaluateResource(envJSRuntime, classLoader, p);
            }
            evaluateResource(envJSRuntime, classLoader, path);

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
                notifier.fireTestFailure(new Failure(description, null));
            }


        } catch (Exception ioException) {
            notifier.fireTestFailure(new Failure(description, ioException));
        }
        notifier.fireTestFinished(description);
    }
}
