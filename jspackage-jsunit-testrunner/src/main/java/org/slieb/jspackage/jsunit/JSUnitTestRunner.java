package org.slieb.jspackage.jsunit;

import com.google.common.collect.ImmutableList;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceFilter;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.ResourceFilters;

import java.lang.annotation.*;
import java.util.List;

import static slieb.kute.resources.ResourceFilters.*;
import static slieb.kute.resources.Resources.filterResources;

/**
 * Runs a jsunit javascript file.
 */
public class JSUnitTestRunner extends ParentRunner<JSUnitSingleTestRunner> {

    private static final ResourceFilter JAVASCRIPT_FILTER = ResourceFilters.extensionFilter(".js");

    private static final ResourceFilter DEFAULT_EXCLUDES = not(any(
            extensionFilter("env.rhino.js"),
            r -> r.getPath().startsWith("jdk/nashorn"),
            r -> r.getPath().endsWith("load.rhino.js"),
            r -> r.getPath().startsWith("com/google/javascript/jscomp"),
            r -> r.getPath().startsWith("com/google/javascript/refactoring"),
            r -> r.getPath().startsWith("/closure-library") && r.getPath().endsWith("_test.js")
    ));

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Configuration {

        String[] includes() default {};

        String[] excludes() default {};

        int timeoutSeconds() default 30;
    }

    public JSUnitTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<JSUnitSingleTestRunner> getChildren() {
        ResourceProvider<Resource.Readable> resources =
                filterResources(Kute.getDefaultProvider(), and(JAVASCRIPT_FILTER, DEFAULT_EXCLUDES));


        ImmutableList.Builder<JSUnitSingleTestRunner> builder = new ImmutableList.Builder<>();
        for (Annotation annotation : getRunnerAnnotations()) {
            if (Configuration.class.isAssignableFrom(annotation.getClass())) {
                Configuration pathsAnnotation = (Configuration) annotation;

                getTestProvider(resources).stream().forEach(testResource -> {
                    System.out.println(testResource);
                    builder.add(new JSUnitSingleTestRunner(resources, testResource, pathsAnnotation.timeoutSeconds()));
                });
            }
        }
        return builder.build();
    }

    private ResourceProvider<Resource.Readable> getTestProvider(ResourceProvider<Resource.Readable> provider) {
        return filterResources(provider, extensionFilter("_test.js"));
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
