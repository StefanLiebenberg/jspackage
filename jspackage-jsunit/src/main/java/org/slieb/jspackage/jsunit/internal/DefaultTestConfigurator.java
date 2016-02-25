package org.slieb.jspackage.jsunit.internal;

import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.jspackage.dependencies.GoogResources;
import org.slieb.jspackage.jsunit.api.TestConfigurator;
import org.slieb.kute.KutePredicates;
import org.slieb.kute.api.Resource;

import static org.slieb.kute.Kute.filterResources;
import static org.slieb.kute.KutePredicates.extensionFilter;

public class DefaultTestConfigurator implements TestConfigurator {

    public static final Resource.Predicate JAVASCRIPT_FILTER = extensionFilter(".js");

    public static final Resource.Predicate DEFAULT_EXCLUDES = KutePredicates.any(
            extensionFilter("env.rhino.js"),
            r -> r.getPath().startsWith("jdk/nashorn"),
            r -> r.getPath().endsWith("load.rhino.js"),
            r -> r.getPath().startsWith("com/google/javascript/jscomp"),
            r -> r.getPath().startsWith("com/google/javascript/refactoring"),
            r -> r.getPath().startsWith("/closure-library") && r.getPath().endsWith("_test.js")
    ).negate()::test;

    public static final Resource.Predicate TESTS_FILTER = extensionFilter("_test.js");

    private final Resource.Provider filteredProvider;
    private final Resource.Provider testProvider;

    public DefaultTestConfigurator(Resource.Provider provider) {
        this.filteredProvider = filterResources(provider, KutePredicates.all(JAVASCRIPT_FILTER, DEFAULT_EXCLUDES));
        this.testProvider = filterResources(this.filteredProvider, TESTS_FILTER);
    }

    @Override
    public Resource.Provider sources() {
        return this.filteredProvider;
    }

    @Override
    public Resource.Provider tests() {
        return this.testProvider;
    }

    @Override
    public GoogDependencyCalculator calculator() {
        return GoogResources.getCalculator(filteredProvider);
    }

    @Override
    public Integer getTimeout() {
        return 30;
    }
}
