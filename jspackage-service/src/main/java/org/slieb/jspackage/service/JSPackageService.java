package org.slieb.jspackage.service;


import org.slieb.jspackage.service.handlers.GeneralExceptionHandler;
import org.slieb.jspackage.service.handlers.RoutesNotFoundExceptionHandler;
import org.slieb.jspackage.service.handlers.ServiceRoute;
import org.slieb.jspackage.service.providers.ServiceProvider;
import spark.Spark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSPackageService {

    private final JSPackageConfiguration configuration;

    private final ServiceProvider provider;

    public JSPackageService(JSPackageConfiguration configuration) {
        this.configuration = configuration;
        this.provider = new ServiceProvider(configuration.getResourceProvider());
    }

    public String getContentType(String path, String defaultType) {
        try {
            String reportedType = Files.probeContentType(Paths.get(path));
            switch (reportedType) {
                case "inode/directory":
                    return defaultType;
                default:
                    return reportedType;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultType;
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

    public static JSPackageService create(JSPackageConfiguration configuration) {
        return new JSPackageService(configuration);
    }

}
