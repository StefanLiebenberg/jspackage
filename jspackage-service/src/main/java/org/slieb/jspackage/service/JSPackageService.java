package org.slieb.jspackage.service;


import com.google.common.base.Preconditions;
import org.slieb.jspackage.service.providers.ServiceProvider;
import slieb.kute.api.Resource;
import spark.Spark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static slieb.kute.resources.Resources.readResource;

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
        Spark.get("/*", (request, response) -> {
            String pathInfo = request.pathInfo();
            Resource.Readable readable = provider.getResourceByName(pathInfo);
            Preconditions.checkNotNull(readable, "resource not found");
            response.type(getContentType(request.pathInfo(), request.contentType()));
            return readResource(readable);
        });


        Spark.exception(RuntimeException.class, (e, request, response) -> {
            e.printStackTrace();

            switch (e.getMessage()) {
                case "resource not found":
                    response.status(404);
                    break;
                default:
                    response.status(500);
            }

            response.type(getContentType(request.pathInfo(), request.contentType()));
            response.body("Exception on " + request.pathInfo() + " : " + e.getMessage());
        });
        Thread.sleep(2000);
    }

    public void stop() {
        Spark.stop();
    }

    public static JSPackageService create(JSPackageConfiguration configuration) {
        return new JSPackageService(configuration);
    }

}
