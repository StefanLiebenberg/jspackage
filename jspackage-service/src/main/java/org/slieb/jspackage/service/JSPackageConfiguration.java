package org.slieb.jspackage.service;


import com.google.common.base.Preconditions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

public class JSPackageConfiguration {

    private final Integer port;

    private final ResourceProvider<? extends Resource.Readable> resourceProvider;

    private JSPackageConfiguration(Integer port, ResourceProvider<? extends Resource.Readable> resourceProvider) {
        this.port = port;
        this.resourceProvider = resourceProvider;
    }

    public Integer getPort() {
        return port;
    }

    public ResourceProvider<? extends Resource.Readable> getResourceProvider() {
        return resourceProvider;
    }

    public static class Builder {

        private Integer port = 6655;

        private ResourceProvider<? extends Resource.Readable> resourceProvider;

        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder withResourceProvider(ResourceProvider<? extends Resource.Readable> resourceProvider) {
            this.resourceProvider = resourceProvider;
            return this;
        }

        public JSPackageConfiguration build() {
            Preconditions.checkNotNull(port, "Port cannot be null");

            return new JSPackageConfiguration(port, resourceProvider);
        }
    }
}
