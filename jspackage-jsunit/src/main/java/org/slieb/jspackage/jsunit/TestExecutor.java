package org.slieb.jspackage.jsunit;

import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.kute.api.Resource;
import org.slieb.runtimes.Runtimes;
import org.slieb.runtimes.rhino.EnvJSRuntime;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.TimeoutException;

public class TestExecutor {

    private final GoogDependencyCalculator calculator;

    private final Resource.Readable testResource;

    private final int timeoutSeconds;

    private long duration;

    private boolean success, executed;

    private String report;

    public TestExecutor(GoogDependencyCalculator calculator,
                        Resource.Readable testResource,
                        int timeoutSeconds) {
        this.calculator = calculator;
        this.testResource = testResource;
        this.timeoutSeconds = timeoutSeconds;
        this.executed = false;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void execute() throws IOException, TimeoutException {
        long start = System.currentTimeMillis();
        try (EnvJSRuntime runtime = new EnvJSRuntime()) {
            runtime.initialize();
            for (Resource.Readable readable : calculator.getResourcesFor(testResource)) {
                try (Reader reader = readable.getReader()) {
                    Runtimes.evaluateReader(runtime, reader, readable.getPath());
                }
            }
            runtime.doLoad();
            if (!JsUnitHelper.isInitialized(runtime)) {
                JsUnitHelper.initialize(runtime);
            }

            while (!JsUnitHelper.isFinished(runtime)) {
                long diff = System.currentTimeMillis() - start;
                if (diff > (timeoutSeconds * 1000)) {
                    throw new TimeoutException("Timed out at " + diff + "ms");
                }
                runtime.doWait(100);
            }

            duration = System.currentTimeMillis() - start;
            success = JsUnitHelper.isSuccess(runtime);
            report = JsUnitHelper.getReport(runtime);
        } catch (IOException e) {
            success = false;
        }
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getReport() {
        return report;
    }
}
