package org.slieb.jspackage.service.providers;

import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.slieb.kute.Kute.findResource;


public abstract class AbstractStreamsProvider implements Resource.Provider {

    protected abstract Stream<Stream<Resource.Readable>> streams();

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        return Kute.findFirstOptionalResource(streams().map(stream -> findResource(stream, path)));
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return streams().flatMap(Function.identity());
    }

}
