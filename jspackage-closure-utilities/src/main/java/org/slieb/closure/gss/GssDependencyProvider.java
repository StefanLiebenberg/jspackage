package org.slieb.closure.gss;

import com.google.common.collect.ImmutableSet;
import org.slieb.kute.api.Resource;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static org.slieb.kute.Kute.findResource;


public class GssDependencyProvider implements Resource.Provider{

    private final Resource.Provider gssResources;

    private final ImmutableSet<String> namespaces;

    public GssDependencyProvider(Resource.Provider gssResources,
                                 Collection<String> namespaces) {
        this.gssResources = gssResources;
        this.namespaces = ImmutableSet.copyOf(namespaces);
    }

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        return findResource(stream(), path);
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return gssResources.stream().map(r -> r);
    }
}
