package org.slieb.closure.gss;


import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Optional;

public interface GssUrlConfiguration {
    @Nonnull
    Optional<URI> getImagesUri();

    @Nonnull
    Optional<URI> getResourcesUri();
}