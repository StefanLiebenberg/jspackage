package org.slieb.closure.javascript;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slieb.dependencies.DependenciesHelper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GoogDependencyHelper<R> implements DependenciesHelper<GoogDependencyNode<R>> {


    @Override
    public List<GoogDependencyNode<R>> getBaselist(Collection<GoogDependencyNode<R>> dependencies) {
        ImmutableList.Builder<GoogDependencyNode<R>> listBuilder = ImmutableList.builder();
        Optional<GoogDependencyNode<R>> optional = dependencies.stream().filter(GoogDependencyNode::isBaseFile).findFirst();
        if (optional.isPresent()) {
            listBuilder.add(optional.get());
        }
        return listBuilder.build();
    }

    @Override
    public Set<GoogDependencyNode<R>> getResolveableSet(Collection<GoogDependencyNode<R>> dependencies) {
        return ImmutableSet.copyOf(dependencies);
    }
}
