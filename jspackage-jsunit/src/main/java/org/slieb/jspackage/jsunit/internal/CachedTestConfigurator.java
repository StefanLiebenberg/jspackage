package org.slieb.jspackage.jsunit.internal;

import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.jspackage.dependencies.GoogDependencyNode;
import org.slieb.jspackage.jsunit.api.TestConfigurator;
import org.slieb.kute.api.Resource;

import java.util.Collection;

public class CachedTestConfigurator implements TestConfigurator {

    private final Resource.Provider sourceProvider, testProvider;

    private final GoogDependencyCalculator calculator;

    private final Integer timeout;

    public CachedTestConfigurator(TestConfigurator testConfigurator) {
        this.sourceProvider = testConfigurator.sources();
        this.testProvider = testConfigurator.tests();
        this.calculator = new CachedCalculator(this.sourceProvider);
        this.timeout = testConfigurator.getTimeout();
    }

    public GoogDependencyCalculator calculator() {
        return calculator;
    }

    @Override
    public Resource.Provider tests() {
        return testProvider;
    }

    @Override
    public Resource.Provider sources() {
        return sourceProvider;
    }

    @Override
    public Integer getTimeout() {
        return timeout;
    }
}

class CachedCalculator extends GoogDependencyCalculator {

    private Collection<GoogDependencyNode> cachedNodes;

    public CachedCalculator(Iterable<Resource.Readable> resources) {
        super(resources);
    }

    @Override
    public Collection<GoogDependencyNode> getDependencyNodes() {
        if (cachedNodes == null) {
            cachedNodes = super.getDependencyNodes();
        }
        return cachedNodes;
    }
}
