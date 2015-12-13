package org.slieb.closure.gss;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Optional;


public class DefaultGssConfiguration implements GssUrlConfiguration {

    @Nonnull
    @Override
    public Optional<URI> getImagesUri() {
        return null;
    }

    @Nonnull
    @Override
    public Optional<URI> getResourcesUri() {
        return null;
    }
}
