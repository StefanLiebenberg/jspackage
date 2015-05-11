package org.slieb.jspackage.service;


import org.slieb.jspackage.service.handlers.GeneralExceptionHandler;
import org.slieb.jspackage.service.handlers.RoutesNotFoundExceptionHandler;
import org.slieb.jspackage.service.handlers.ServiceRoute;
import org.slieb.jspackage.service.providers.ServiceProvider;
import slieb.kute.api.Resource;
import spark.Spark;

import java.util.stream.Stream;

public class JSPackageService {

    private final JSPackageConfiguration configuration;

    private final ServiceProvider provider;

    public JSPackageService(JSPackageConfiguration configuration) {
        this.configuration = configuration;
        this.provider = new ServiceProvider(configuration.getResourceProvider());
    }


    public void start() throws InterruptedException {
        Spark.port(configuration.getPort());
        Spark.get("/*", new ServiceRoute(provider));
        Spark.exception(RoutesNotFoundExceptionHandler.ResourceNotFound.class, new RoutesNotFoundExceptionHandler());
        Spark.exception(RuntimeException.class, new GeneralExceptionHandler());
        Thread.sleep(1000);
    }

    public void stop() {
        Spark.stop();
    }

    public Stream<Resource.Readable> stream() {
        return provider.stream();
    }

    public static JSPackageService create(JSPackageConfiguration configuration) {
        return new JSPackageService(configuration);
    }

}
