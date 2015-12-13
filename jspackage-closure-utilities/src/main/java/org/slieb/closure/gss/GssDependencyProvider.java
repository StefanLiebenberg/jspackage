package org.slieb.closure.gss;

import com.google.common.collect.ImmutableSet;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static slieb.kute.Kute.findResource;


public class GssDependencyProvider implements ResourceProvider<Resource.Readable> {

    private final ResourceProvider<? extends Resource.Readable> gssResources;

    private final ImmutableSet<String> namespaces;

    public GssDependencyProvider(ResourceProvider<? extends Resource.Readable> gssResources,
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
