package org.slieb.jspackage.jsunit.api;

import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.kute.api.Resource;

public interface TestConfigurator {

    /**
     * @return A Resource provider that contains all relevant sources.
     */
    Resource.Provider sources();

    /**
     * @return A provider that gives you the tests.
     */
    Resource.Provider tests();

    /**
     * @return A calculator instance.
     */
    GoogDependencyCalculator calculator();

    Integer getTimeout();
}
