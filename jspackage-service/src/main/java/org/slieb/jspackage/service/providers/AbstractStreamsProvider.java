package org.slieb.jspackage.service.providers;

import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.util.stream.Stream;


public abstract class AbstractStreamsProvider implements ResourceProvider<Resource.Readable> {

    protected abstract Stream<Stream<? extends Resource.Readable>> streams();

    @Override
    public Resource.Readable getResourceByName(String path) {
        return Resources.findFirstResource(streams().map(s -> Resources.findResource(s, path)));
    }


    @Override
    public Stream<Resource.Readable> stream() {
        return streams().flatMap(s -> s);
    }


}
