package org.slieb.jspackage.service.resources;

import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;


public class IndexResource extends AbstractHtmlResource {

    private final String basePath;

    private final ResourceProvider<Resource.Readable> resources;

    public IndexResource(String path, ResourceProvider<Readable> resources) {
        this.basePath = path;
        this.resources = resources;
    }

    @Override
    public String getPath() {
        return basePath;
    }

    public String getIndexContent() {
        StringBuilder str = new StringBuilder();
        resources.stream()
                .map(Resource::getPath)
                .filter(this::filterResourceByPath)
                .map(this::getResourceEntryFromPath)
                .forEach(str::append);
        return str.toString();
    }

    public Boolean filterResourceByPath(String path) {
        return true;
    }


    @Override
    public String getHtmlContent() {
        return String.format("<!DOCTYPE html><html><body><h1>Index of %s</h1><ul>%s</ul></body></html>", basePath, this.getIndexContent());
    }

    private String getResourceEntryFromPath(String path) {
        return String.format("<li><a href='%s'>%s</a></li>", path, path);
    }
}
