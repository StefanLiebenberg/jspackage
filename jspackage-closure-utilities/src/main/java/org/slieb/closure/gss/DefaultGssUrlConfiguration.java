package org.slieb.closure.gss;


import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Optional;

public class DefaultGssUrlConfiguration implements GssUrlConfiguration {

    private final URI images, resources;

    public DefaultGssUrlConfiguration(URI images, URI resources) {
        this.images = images;
        this.resources = resources;
    }

    @Nonnull
    @Override
    public Optional<URI> getImagesUri() {
        return Optional.ofNullable(images);
    }

    @Nonnull
    @Override
    public Optional<URI> getResourcesUri() {
        return Optional.ofNullable(resources);
    }
}
