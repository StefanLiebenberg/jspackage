package org.slieb.jspackage.service.providers;

import org.slieb.closure.javascript.GoogResources;
import org.slieb.jspackage.service.resources.ComponentTestResource;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.stream.Stream;


public class ComponentTestsProvider implements ResourceProvider<Resource.Readable> {

    private final ResourceProvider<? extends Resource.Readable> resources;


    public ComponentTestsProvider(ResourceProvider<? extends Resource.Readable> resources) {
        this.resources = resources;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return resources.stream().filter(this::filterResource).map(this::componentTestResource);
    }

    @Override
    public Resource.Readable getResourceByName(String path) {
        Resource.Readable readable = resources.getResourceByName(reverseComponentTestPath(path));
        if (readable != null && this.filterResource(readable)) {
            return componentTestResource(readable);
        } else {
            return null;
        }
    }

    private Boolean filterResource(Resource.Readable readable) {
        return readable.getPath().endsWith("_test.js");
    }

    private ComponentTestResource componentTestResource(Resource.Readable resource) {
        return new ComponentTestResource(resource, resources, getComponentTestPath(resource));
    }

    private String getComponentTestPath(Resource.Readable readable) {
        return readable.getPath().replace("_test.js", "_test.html");
    }

    private String reverseComponentTestPath(String path) {
        return path.replace("_test.html", "_test.js");
    }
}
