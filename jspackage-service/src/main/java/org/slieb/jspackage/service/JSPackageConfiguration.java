package org.slieb.jspackage.service;


import org.slieb.kute.api.Resource;

import java.net.URI;

public class JSPackageConfiguration {

    private final Integer port;

    private final Resource.Provider resourceProvider;

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
                                     Resource.Provider resourceProvider,
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

    public Resource.Provider getResourceProvider() {
        return resourceProvider;
    }


}

