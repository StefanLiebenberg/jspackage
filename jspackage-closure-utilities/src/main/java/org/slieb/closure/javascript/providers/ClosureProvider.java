package org.slieb.closure.javascript.providers;


import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogDependencyParser;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.closure.javascript.internal.DepsFileBuilder;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.implementations.StringSupplierResource;

import java.util.stream.Stream;

public class ClosureProvider implements ResourceProvider<Resource.Readable> {

    public static final String DEPS = "/deps.js", DEFINES = "/defines.js";

    private final GoogDependencyCalculator calculator;

    private final ResourceProvider<? extends Resource.Readable> provider;

    private final GoogDependencyParser parser;


    /**
     * @param provider
     * @param parser
     */
    public ClosureProvider(ResourceProvider<? extends Resource.Readable> provider, GoogDependencyParser parser) {
        this.provider = provider;
        this.parser = GoogResources.getDependencyParser();
        this.calculator = GoogResources.getCalculatorCast(provider);
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
