package org.slieb.closure.javascript.providers;


import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.closure.javascript.internal.DepsFileBuilder;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.implementations.StringSupplierResource;

import java.util.Optional;
import java.util.stream.Stream;

public class ClosureProvider implements ResourceProvider<Resource.Readable> {

    public static final String DEPS = "/deps.js", DEFINES = "/defines.js";

    private final GoogDependencyCalculator calculator;

    private final ResourceProvider<? extends Resource.Readable> provider;


    /**
     * @param provider
     */
    public ClosureProvider(ResourceProvider<Resource.Readable> provider) {
        this.provider = provider;
        this.calculator = GoogResources.getCalculator(provider);
    }

    private Resource.Readable getDependencyResource() {
        return new StringSupplierResource("/deps.js", new DepsFileBuilder(provider)::getDependencyContent);
    }

    private Resource.Readable getDefinesResource() {
        return new StringSupplierResource("/defines.js", "");
    }

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        if (DEPS.equals(path)) {
            return Optional.of(getDependencyResource());
        }

        if (DEFINES.equals(path)) {
            return Optional.of(getDefinesResource());
        }

        return Optional.empty();
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.of(getDependencyResource(), getDefinesResource());
    }

}
