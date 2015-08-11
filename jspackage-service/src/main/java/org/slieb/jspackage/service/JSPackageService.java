package org.slieb.jspackage.service;


import com.google.common.collect.ImmutableList;
import org.slieb.jspackage.service.providers.ServiceProvider;
import org.slieb.kute.service.Service;
import org.slieb.kute.service.providers.IndexProvider;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.util.stream.Stream;

public class JSPackageService {

    private final JSPackageConfiguration configuration;

    private final ResourceProvider<? extends Resource.Readable> provider, indexer, grouped;

    private final Service service;

    public JSPackageService(JSPackageConfiguration configuration) {
        this.configuration = configuration;
        this.provider = new ServiceProvider(configuration.getResourceProvider());
        this.indexer = new IndexProvider(this.provider);
        this.grouped = new GroupResourceProvider<>(ImmutableList.of(this.provider, this.indexer));
        this.service = new Service(this.grouped, this.configuration.getPort());
    }


    public void start() throws InterruptedException {
        this.service.start();
    }

    public void stop() {
        this.service.stop();
    }

    public Stream<? extends Resource.Readable> stream() {
        return provider.stream();
    }

    public static JSPackageService create(JSPackageConfiguration configuration) {
        return new JSPackageService(configuration);
    }

    public boolean stopped() {
        return service.stopped();
    }

}
