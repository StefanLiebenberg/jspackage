package org.slieb.jspackage.service;

import com.google.common.base.Preconditions;
import org.slieb.kute.api.Resource;


public class JSPackageConfigurationBuilder {

    private Integer port = 6655;

    private Resource.Provider resourceProvider;

    public JSPackageConfigurationBuilder withPort(Integer port) {
        this.port = port;
        return this;
    }

    public JSPackageConfigurationBuilder withResourceProvider(Resource.Provider resourceProvider) {
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
