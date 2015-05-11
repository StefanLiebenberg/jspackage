package org.slieb.closure.javascript.providers;


import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.closure.javascript.GoogDependencyParser;
import org.slieb.closure.javascript.GoogResources;
import org.slieb.closure.javascript.internal.DepsFileBuilder;
import org.slieb.dependencies.DependencyCalculator;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.implementations.StringSupplierResource;

import java.util.stream.Stream;

public class ClosureProvider implements ResourceProvider<Resource.Readable> {

    public static final String DEPS = "/deps.js", DEFINES = "/defines.js";

    private final DependencyCalculator<Resource.Readable, GoogDependencyNode<Resource.Readable>> calculator;

    private final ResourceProvider<? extends Resource.Readable> provider;

    private final GoogDependencyParser<Resource.Readable> parser;
//
//    private final ResourceProvider<? extends Resource.Readable> templatesProvider;
//
//    private final ResourceProvider<? extends Resource.InputStreaming> assetsProvider;


    /**
     * @param provider
     * @param parser
     */
    public ClosureProvider(ResourceProvider<? extends Resource.Readable> provider, GoogDependencyParser<Resource.Readable> parser) {
        this.provider = provider;
        this.parser = GoogResources.getDependencyParser();
        this.calculator = GoogResources.getCalculator(provider);
//        this.filteredJsFiles = Resources.filterResources(provider, ResourcePredicates.extensionFilter(".js"));
//        this.filteredSoyFiles = Resources.filterResources(provider, ResourcePredicates.extensionFilter(".soy"));
    }

    private Resource.Readable getDependencyResource() {
        return new StringSupplierResource("/deps.js", new DepsFileBuilder(provider, parser)::getDependencyContent);
    }

    private Resource.Readable getDefinesResource() {
        return new StringSupplierResource("/defines.js", "");
    }

    @Override
    public Resource.Readable getResourceByName(String path) {
        if (DEPS.equals(path)) {
            return getDependencyResource();
        }

        if (DEFINES.equals(path)) {
            return getDefinesResource();
        }

        return null;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.of(getDependencyResource(), getDefinesResource());
    }

}
