package org.slieb.jspackage.service;

import com.google.common.base.Preconditions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;


public class JSPackageConfigurationBuilder {

    private Integer port = 6655;

    private ResourceProvider<Resource.Readable> resourceProvider;

    public JSPackageConfigurationBuilder withPort(Integer port) {
        this.port = port;
        return this;
    }

    public JSPackageConfigurationBuilder withResourceProvider(ResourceProvider<Resource.Readable> resourceProvider) {
        this.resourceProvider = resourceProvider;
        return this;
    }

    public JSPackageConfiguration build() {
        Preconditions.checkNotNull(port, "Port cannot be null");
        return new JSPackageConfiguration(port, resourceProvider, null, null);
    }

    public static JSPackageConfigurationBuilder aJSPackageConfiguration() {
        return new JSPackageConfigurationBuilder();
    }
}
