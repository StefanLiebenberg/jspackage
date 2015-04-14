package org.slieb.closure.javascript;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slieb.dependencies.DependenciesHelper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GoogDependencyHelper implements DependenciesHelper<GoogDependencyNode> {


    @Override
    public List<GoogDependencyNode> getBaselist(Collection<GoogDependencyNode> dependencies) {
        ImmutableList.Builder<GoogDependencyNode> listBuilder = ImmutableList.builder();
        Optional<GoogDependencyNode> optional = dependencies.stream().filter(GoogDependencyNode::isBaseFile).findFirst();
        if (optional.isPresent()) {
            listBuilder.add(optional.get());
        }
        return listBuilder.build();
    }

    @Override
    public Set<GoogDependencyNode> getResolveableSet(Collection<GoogDependencyNode> dependencies) {
        return ImmutableSet.copyOf(dependencies);
    }
}
