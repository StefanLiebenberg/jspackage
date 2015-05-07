package org.slieb.jspackage.service.resources;

import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.closure.javascript.GoogDependencyParser;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.ResourceFilters;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.slieb.closure.javascript.GoogResources.getDependencyParser;
import static slieb.kute.resources.Resources.filterResources;


public class DepsResource extends AbstractHtmlResource {

    private static final GoogDependencyParser<Resource.Readable> parser = getDependencyParser();

    private final ResourceProvider<? extends Resource.Readable> jsReadables;

    private final String path;

    public DepsResource(ResourceProvider<? extends Readable> jsReadables, String path) {
        this.jsReadables = filterResources(jsReadables, ResourceFilters.extensionFilter(".js"));
        this.path = path;
    }

    @Override
    public String getHtmlContent() {
        Set<GoogDependencyNode<Readable>> deps =
                jsReadables.stream().map(parser::parse).collect(Collectors.toSet());

        String basePath =
                deps.stream()
                        .filter(GoogDependencyNode::isBaseFile)
                        .map(GoogDependencyNode::getResource)
                        .map(Resource::getPath)
                        .findFirst().orElseThrow(() -> new RuntimeException("No Base Dep"));

        StringBuilder builder = new StringBuilder();
        deps.stream().filter(d -> !d.isBaseFile())
                .map(d -> this.getDependency(d, basePath))
                .forEach(builder::append);

        return builder.toString();
    }

    private String getDependency(GoogDependencyNode<Readable> dependencyNode, String basePath) {
        return String.format("goog.addDependency('%s', %s, %s);\n", relative(dependencyNode.getResource().getPath(), basePath), getArgs(dependencyNode.getProvides()),
                getArgs(dependencyNode.getRequires()));
    }

    private String relative(String a, String base) {
        return Paths.get(base).relativize(Paths.get(a)).toString();
    }

    private String getArgs(Set<String> args) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = args.iterator();
        stringBuilder.append("[");
        while (iterator.hasNext()) {
            stringBuilder.append("'").append(iterator.next()).append("'");
            if (iterator.hasNext()) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.append("]").toString();

    }

    @Override
    public String getPath() {
        return path;
    }
}
