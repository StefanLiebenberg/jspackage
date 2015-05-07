package org.slieb.jspackage.service.resources;


import org.slieb.closure.javascript.GoogResources;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

public class ComponentTestResource extends AbstractHtmlResource {


    public final Resource.Readable testResource;

    public final ResourceProvider<? extends Resource.Readable> sources;

    private final String path;

    public ComponentTestResource(Readable testResource, ResourceProvider<? extends Readable> sources, String path) {
        this.testResource = testResource;
        this.sources = sources;
        this.path = path;
    }

    public String getHtmlContent() {
        return String.format("<!DOCTYPE html><html><head>%s</head></html>", getIndexContent());
    }

    public String getIndexContent() {
        final StringBuilder buffer = new StringBuilder();
        GoogResources.getCalculator(sources)
                .getResourcesFor(testResource)
                .stream()
                .sequential()
                .map(this::getResourceIndex).forEach(buffer::append);
        return buffer.toString();
    }

    private String getResourceIndex(Resource.Readable readable) {
        return String.format("<script src='%s'></script>", readable.getPath());
    }


    @Override
    public String getPath() {
        return path;
    }
}
