package org.slieb.jspackage.service;

import org.slieb.jspackage.service.providers.IndexProvider;
import org.slieb.jspackage.service.providers.ServiceProvider;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;
import org.slieb.sparks.SparkWrapper;

import java.util.stream.Stream;

public class JSPackageService {

    private final JSPackageConfiguration configuration;
    private final SparkWrapper spark;

    private final ServiceProvider provider;
    private final Resource.Provider indexer;
    private final Resource.Provider grouped;
    //

    //    private final KuteService service;
    //
    public JSPackageService(JSPackageConfiguration configuration) {
        this.configuration = configuration;
        this.spark = new SparkWrapper("localhost", this.configuration.getPort());
        this.provider = new ServiceProvider(configuration.getResourceProvider());
        this.indexer = new IndexProvider(this.provider);
        this.grouped = Kute.group(this.provider, this.indexer);

        //        this.service = new KuteService(this.spark.getSparkInstance());
    }

    //
    public void start() throws InterruptedException {

        //        this.service.start();
        //        this.service.addResourceProvider(this.grouped);
    }

    //
    public void stop() {
        //        this.service.stop();
    }

    //
    public Stream<Resource.Readable> stream() {
        //        return provider.stream();
        return Stream.empty();
    }

    //
    public static JSPackageService create(JSPackageConfiguration configuration) {
        return new JSPackageService(configuration);
    }

    //
    public boolean stopped() {
        //            return service.stopped();
        return true;
    }
}
