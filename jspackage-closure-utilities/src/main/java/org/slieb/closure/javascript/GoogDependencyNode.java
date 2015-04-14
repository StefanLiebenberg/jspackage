package org.slieb.closure.javascript;


import com.google.common.collect.ImmutableSet;
import com.google.javascript.jscomp.SourceFile;
import org.slieb.dependencies.DependencyNode;

import java.util.Set;

public class GoogDependencyNode implements DependencyNode<SourceFile> {

    private final SourceFile sourceFile;

    private final ImmutableSet<String> provides, requires;

    private final Boolean isBaseFile;

    public GoogDependencyNode(SourceFile sourceFile, ImmutableSet<String> provides, ImmutableSet<String> requires, Boolean isBaseFile) {
        this.sourceFile = sourceFile;
        this.provides = provides;
        this.requires = requires;
        this.isBaseFile = isBaseFile;
    }

    @Override
    public SourceFile getResource() {
        return sourceFile;
    }

    public Boolean isBaseFile() {
        return isBaseFile;
    }

    @Override
    public Set<String> getRequires() {
        return null;
    }

    @Override
    public Set<String> getProvides() {
        return null;
    }
}
