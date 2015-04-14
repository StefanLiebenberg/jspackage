package org.slieb.closure.javascript;


import com.google.common.collect.ImmutableSet;
import org.slieb.dependencies.DependencyNode;

public class GoogDependencyNode<R> implements DependencyNode<R> {

    private final R resource;

    private final ImmutableSet<String> provides, requires;

    private final Boolean isBaseFile;

    public GoogDependencyNode(R resource, ImmutableSet<String> provides, ImmutableSet<String> requires, Boolean isBaseFile) {
        this.resource = resource;
        this.provides = provides;
        this.requires = requires;
        this.isBaseFile = isBaseFile;
    }

    @Override
    public R getResource() {
        return resource;
    }

    public Boolean isBaseFile() {
        return isBaseFile;
    }

    @Override
    public ImmutableSet<String> getRequires() {
        return requires;
    }

    @Override
    public ImmutableSet<String> getProvides() {
        return provides;
    }
}
