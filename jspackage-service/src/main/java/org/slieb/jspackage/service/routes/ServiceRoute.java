package org.slieb.jspackage.service.routes;

import org.apache.commons.io.IOUtils;
import org.slieb.jspackage.service.handlers.RoutesNotFoundExceptionHandler;
import org.slieb.sparks.Sparks;
import org.slieb.kute.api.Resource;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ServiceRoute implements Route {

    private final Resource.Provider provider;

    public ServiceRoute(Resource.Provider provider) {
        this.provider = provider;
    }


    @Override
    public Object handle(Request request,
                         Response response) throws Exception {
        long startTime = System.nanoTime();

        try {
            response.type(getContentType(request, response));
            Resource.Readable resource = getReadable(request);
            try (InputStream inputStream = resource.getInputStream();
                 OutputStream outputStream = response.raw().getOutputStream()) {
                IOUtils.copy(inputStream, outputStream);
            }
            return "";
        } finally {
            System.out.println(request.pathInfo() + ": " + (System.nanoTime() - startTime) / 1000000);
        }
    }

    public Resource.Readable getReadable(Request request) throws RoutesNotFoundExceptionHandler.ResourceNotFound {
        return provider.getResourceByName(request.pathInfo()).orElseThrow(
                RoutesNotFoundExceptionHandler.ResourceNotFound::new);
    }

    public String getContentType(Request request,
                                 Response response) throws IOException {
        return Sparks.getContentType(request);
    }
}
