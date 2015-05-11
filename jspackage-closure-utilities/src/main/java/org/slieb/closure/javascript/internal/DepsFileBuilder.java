package org.slieb.closure.javascript.internal;


import com.google.common.base.Joiner;
import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.closure.javascript.GoogDependencyParser;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class DepsFileBuilder {

    private final ResourceProvider<? extends Resource.Readable> resourceProvider;

    private final GoogDependencyParser<Resource.Readable> parser;

    public DepsFileBuilder(ResourceProvider<? extends Resource.Readable> resourceProvider, GoogDependencyParser<Resource.Readable> parser) {
        this.resourceProvider = resourceProvider;
        this.parser = parser;
    }

    public String getDependencyContent() {
        
        Set<GoogDependencyNode<Resource.Readable>> nodes =
                resourceProvider.stream()
                        .parallel()
                        .filter(r -> r.getPath().endsWith(".js"))
                        .map(parser::parse).collect(toSet());

        Path basePath = nodes.stream()
                .filter(GoogDependencyNode::isBaseFile)
                .map(GoogDependencyNode::getResource)
                .map(Resource::getPath)
                .map(Paths::get).findFirst().get();

        StringBuffer stringBuffer = new StringBuffer();
        nodes.stream()
                .filter(n -> !n.isBaseFile() && !n.getProvides().isEmpty())
                .map(n -> getDependencyLine(n, basePath))
                .forEach(stringBuffer::append);
        return stringBuffer.toString();
    }

    private String getDependencyLine(GoogDependencyNode<Resource.Readable> dependencyNode, Path basePath) {
        return String.format("goog.addDependency(%s, %s, %s);\n",
                this.wrapString(getNodePath(dependencyNode.getResource(), basePath)),
                getStringArray(dependencyNode.getProvides()),
                getStringArray(dependencyNode.getRequires()));
    }

    private String getNodePath(Resource resource, Path basePath) {
        return basePath.getParent().relativize(Paths.get(resource.getPath())).toString();
    }

    private String getStringArray(Collection<String> strings) {
        return String.format("[%s]", Joiner.on(", ").join(strings.stream().map(this::wrapString).toArray(String[]::new)));
    }

    private String wrapString(String content) {
        return String.format("'%s'", content);
    }
}
