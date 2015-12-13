package org.slieb.jspackage.service.providers;

import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static slieb.kute.Kute.findResource;


public abstract class AbstractStreamsProvider<T extends Resource.Readable> implements ResourceProvider<T> {

    protected abstract Stream<Stream<T>> streams();

    @Override
    public Optional<T> getResourceByName(String path) {
        return Kute.findFirstOptionalResource(streams().map(stream -> findResource(stream, path)));
    }


    @Override
    public Stream<T> stream() {
        return streams().flatMap(Function.identity());
    }


}
