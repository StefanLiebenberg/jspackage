package org.slieb.closure.javascript.providers;

//import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
//import org.slieb.jspackage.dependencies.GoogResources;

import org.slieb.closure.javascript.internal.DepsFileBuilder;
import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.jspackage.dependencies.GoogResources;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;

import java.util.Optional;
import java.util.stream.Stream;

//import org.slieb.kute.Kute;

public class ClosureProvider implements Resource.Provider {

    public static final String DEPS = "/deps.js", DEFINES = "/defines.js";

    private final GoogDependencyCalculator calculator;

    private final Resource.Provider provider;


    /**
     * @param provider
     */
    public ClosureProvider(Resource.Provider provider) {
        this.provider = provider;
        this.calculator = GoogResources.getCalculator(provider);
    }

    private Resource.Readable getDependencyResource() {
        return Kute.stringResource("/deps.js", new DepsFileBuilder(provider)::getDependencyContent);
    }

    private Resource.Readable getDefinesResource() {
        return Kute.stringResource("/defines.js", "");
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
