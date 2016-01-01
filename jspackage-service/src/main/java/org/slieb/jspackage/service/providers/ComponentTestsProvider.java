package org.slieb.jspackage.service.providers;

import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.jspackage.service.resources.ComponentTestResource;
import slieb.kute.api.Resource;

import java.util.Optional;
import java.util.stream.Stream;

import static slieb.kute.Kute.filterResources;
import static slieb.kute.KuteLambdas.extensionFilter;


public class ComponentTestsProvider implements Resource.Provider {

    private final Resource.Provider resources;

    private final GoogDependencyCalculator calculator;

    public ComponentTestsProvider(Resource.Provider resources) {
        this.resources = resources;
        this.calculator = GoogResources.getCalculator(filterResources(resources, extensionFilter(".js")));
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return resources.stream().filter(this::filterResource).map(this::componentTestResource);
    }

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        return resources.getResourceByName(reverseComponentTestPath(path))
                .filter(this::filterResource)
                .map(this::componentTestResource);
    }

    private Boolean filterResource(Resource.Readable readable) {
        return readable.getPath().endsWith("_test.js");
    }

    private ComponentTestResource componentTestResource(Resource.Readable resource) {
        return new ComponentTestResource(getComponentTestPath(resource), resource, calculator);
    }

    private String getComponentTestPath(Resource.Readable readable) {
        return readable.getPath().replace("_test.js", "_test.html");
    }

    private String reverseComponentTestPath(String path) {
        return path.replace("_test.html", "_test.js");
    }
}
