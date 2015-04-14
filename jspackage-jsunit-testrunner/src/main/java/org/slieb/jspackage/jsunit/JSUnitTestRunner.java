package org.slieb.jspackage.jsunit;

import com.google.common.collect.ImmutableList;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.*;
import java.util.List;

/**
 * Runs a jsunit javascript file.
 */
public class JSUnitTestRunner extends ParentRunner<JSUnitSingleTestRunner> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Configuration {
        String[] value();

        String[] sources() default {};

        int timeoutSeconds() default 30;
    }
    
    public JSUnitTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<JSUnitSingleTestRunner> getChildren() {
        ImmutableList.Builder<JSUnitSingleTestRunner> builder = new ImmutableList.Builder<>();
        for (Annotation annotation : getRunnerAnnotations()) {
            if (Configuration.class.isAssignableFrom(annotation.getClass())) {
                Configuration pathsAnnotation = (Configuration) annotation;
                for (String path : pathsAnnotation.value()) {
                    builder.add(new JSUnitSingleTestRunner(path, pathsAnnotation.sources(), pathsAnnotation.timeoutSeconds()));
                }
            }
        }

        return builder.build();
    }

    @Override
    protected Description describeChild(JSUnitSingleTestRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(JSUnitSingleTestRunner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
