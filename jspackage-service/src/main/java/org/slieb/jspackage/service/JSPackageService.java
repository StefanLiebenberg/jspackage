package org.slieb.jspackage.service;


import com.google.common.collect.ImmutableList;
import org.slieb.jspackage.service.providers.ServiceProvider;
import org.slieb.kute.service.KuteService;
import org.slieb.kute.service.providers.IndexProvider;
import org.slieb.sparks.SparkWrapper;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.util.stream.Stream;

public class JSPackageService {

    private final JSPackageConfiguration configuration;

    private final ServiceProvider provider;
    private final ResourceProvider<Resource.Readable> indexer;
    private final ResourceProvider<Resource.Readable> grouped;

    private final SparkWrapper spark;
    private final KuteService service;

    public JSPackageService(JSPackageConfiguration configuration) {
        this.configuration = configuration;
        this.provider = new ServiceProvider(configuration.getResourceProvider());
        this.indexer = new IndexProvider(this.provider);
        this.grouped = new GroupResourceProvider<>(ImmutableList.of(this.provider, this.indexer));
        this.spark = new SparkWrapper("localhost", this.configuration.getPort());
        this.service = new KuteService(this.spark.getSparkInstance());
    }


    public void start() throws InterruptedException {
        this.service.start();
        this.service.addResourceProvider(this.grouped);
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
