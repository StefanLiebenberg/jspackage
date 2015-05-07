package org.slieb.jspackage.service.providers;

import org.slieb.jspackage.service.resources.AllTestsResource;
import org.slieb.jspackage.service.resources.DepsResource;
import org.slieb.jspackage.service.resources.IndexResource;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.stream.Stream;


public class ServiceProvider implements ResourceProvider<Resource.Readable> {

    private final static String INDEX = "/", ALL_TESTS = "/testing/all_tests.html";

    private final ResourceProvider<? extends Resource.Readable> sources;

    private final ComponentTestsProvider testsProvider;


    public ServiceProvider(ResourceProvider<? extends Resource.Readable> sources) {
        this.sources = sources;
        this.testsProvider = new ComponentTestsProvider(this.sources);
    }


    private Resource.Readable getIndexResource() {
        return new IndexResource(INDEX, this);
    }

    private AllTestsResource getAllTestsResource() {
        return new AllTestsResource(ALL_TESTS, this);
    }

    private DepsResource getDepsResource() {
        return new DepsResource(sources, getDepsPath());
    }

    private String getDepsPath() {
        return sources.stream().map(Resource::getPath).filter(r -> r.endsWith("/deps.js")).findFirst().orElseThrow(() -> new RuntimeException("no deps?"));
    }

    private Boolean isDepsResource(String path) {
        return path.endsWith("/deps.js");
    }

    @Override
    public Resource.Readable getResourceByName(String path) {
        Resource.Readable readable = sources.getResourceByName(path);
        if (readable != null) {
            if (isDepsResource(path)) {
                return getDepsResource();
            }
            return readable;
        }

        if (INDEX.equals(path)) {
            return getIndexResource();
        }

        if (ALL_TESTS.equals(path)) {
            return getAllTestsResource();
        }

        return testsProvider.getResourceByName(path);
    }


    @Override
    public Stream<Resource.Readable> stream() {
        // not dealing distinct fair here.
        return Stream.concat(Stream.concat(sources.stream(), testsProvider.stream()), Stream.of(getIndexResource(), getAllTestsResource(), getDepsResource())).distinct();
    }
}
