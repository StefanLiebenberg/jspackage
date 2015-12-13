package org.slieb.jspackage.service;


import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.net.URI;

public class JSPackageConfiguration {

    private final Integer port;

    private final ResourceProvider<Resource.Readable> resourceProvider;

    public final URI resourcesPath, imagePath;


    /**
     * Use the builder.
     *
     * @param port
     * @param resourceProvider
     * @param resourcesPath
     * @param imagePath
     */
    protected JSPackageConfiguration(Integer port,
                                     ResourceProvider<Resource.Readable> resourceProvider,
                                     URI resourcesPath,
                                     URI imagePath) {
        this.port = port;
        this.resourceProvider = resourceProvider;
        this.resourcesPath = resourcesPath;
        this.imagePath = imagePath;
    }

    public Integer getPort() {
        return port;
    }

    public ResourceProvider<Resource.Readable> getResourceProvider() {
        return resourceProvider;
    }


}

