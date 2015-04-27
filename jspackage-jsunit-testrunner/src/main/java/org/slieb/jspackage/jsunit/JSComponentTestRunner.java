package org.slieb.jspackage.jsunit;

import com.google.common.collect.ImmutableList;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;


public class JSComponentTestRunner extends ParentRunner<JSComponentSingleTestRunner> {

    /**
     * Constructs a new {@code ParentRunner} that will run {@code @TestClass}
     *
     * @param testClass
     */
    protected JSComponentTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<JSComponentSingleTestRunner> getChildren() {
        return ImmutableList.of();
    }

    @Override
    protected Description describeChild(JSComponentSingleTestRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(JSComponentSingleTestRunner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
