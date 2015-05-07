package org.slieb.jspackage.service.handlers;

import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static slieb.kute.resources.Resources.readResource;


public class ServiceRoute implements Route {

    private final ResourceProvider<Resource.Readable> provider;

    public ServiceRoute(ResourceProvider<Resource.Readable> provider) {
        this.provider = provider;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type(getContentType(request, response));
        return readResource(getReadable(request));
    }

    public Resource.Readable getReadable(Request request) throws RoutesNotFoundExceptionHandler.ResourceNotFound {
        Resource.Readable readable = provider.getResourceByName(request.pathInfo());
        if (readable != null) {
            return readable;
        }
        throw new RoutesNotFoundExceptionHandler.ResourceNotFound();
    }

    public String getContentType(Request request, Response response) throws IOException {
        String path = request.pathInfo();
        if (path.endsWith("/")) {
            return "text/html";
        }

        return Files.probeContentType(Paths.get(path));
    }
}
