package org.slieb.jspackage.jsunit.internal;

import org.codehaus.plexus.util.MatchPatterns;
import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.jspackage.dependencies.GoogResources;
import org.slieb.jspackage.jsunit.api.JsUnitConfig;
import org.slieb.jspackage.jsunit.api.TestConfigurator;
import org.slieb.kute.KutePredicates;
import org.slieb.kute.api.Resource;

import static org.slieb.kute.Kute.filterResources;

public class AnnotatedTestConfigurator implements TestConfigurator {

    private final Resource.Provider defaultProvider, defaultTestProvider;

    private final GoogDependencyCalculator calc;

    private final Integer timeout;

    public AnnotatedTestConfigurator(JsUnitConfig config,
                                     Resource.Provider provider) {
        final Resource.Predicate predicate = getPredicate(config.includes(), config.excludes());
        final Resource.Predicate all = KutePredicates.all(
                DefaultTestConfigurator.JAVASCRIPT_FILTER,
                DefaultTestConfigurator.DEFAULT_EXCLUDES,
                predicate);
        this.defaultProvider = filterResources(provider, all);

        this.defaultTestProvider = filterResources(this.defaultProvider,
                                                   KutePredicates.all(DefaultTestConfigurator.TESTS_FILTER,
                                                                      getPredicate(config.testIncludes(), config.testExcludes())));
        this.calc = GoogResources.getCalculator(this.defaultProvider);
        this.timeout = config.timeout();
    }

    @Override
    public Resource.Provider sources() {
        return defaultProvider;
    }

    @Override
    public Resource.Provider tests() {
        return defaultTestProvider;
    }

    @Override
    public Integer getTimeout() {
        return timeout;
    }

    @Override
    public GoogDependencyCalculator calculator() {
        return calc;
    }

    private static Resource.Predicate getPredicate(String[] includes,
                                                   String[] excludes) {

        Resource.Predicate predicate = (r) -> true;

        boolean shouldUseInclude = includes.length > 0, shouldUseExclude = excludes.length > 0;
        if (!shouldUseInclude && !shouldUseExclude) {
            return predicate;
        }

        if (shouldUseInclude) {
            MatchPatterns includePatterns = MatchPatterns.from(includes);
            predicate = resource -> includePatterns.matches(resource.getPath(), true);
        }

        if (shouldUseExclude) {
            MatchPatterns excludePatterns = MatchPatterns.from(excludes);
            predicate = KutePredicates.all(predicate, resource -> !excludePatterns.matches(resource.getPath(), true));
        }

        return predicate;
    }
}

