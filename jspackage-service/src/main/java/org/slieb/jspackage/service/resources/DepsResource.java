package org.slieb.jspackage.service.resources;

import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.javascript.internal.DepsFileBuilder;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import static slieb.kute.resources.ResourcePredicates.extensionFilter;
import static slieb.kute.resources.Resources.filterResources;


public class DepsResource extends AbstractHtmlResource {


    private final ResourceProvider<? extends Resource.Readable> jsReadables;

    private final String path;

    public DepsResource(ResourceProvider<? extends Readable> jsReadables, String path) {
        this.jsReadables = filterResources(jsReadables, extensionFilter(".js"));
        this.path = path;
    }

    @Override
    public String getHtmlContent() {
        return new DepsFileBuilder(jsReadables, GoogDependencyCalculator.PARSER).getDependencyContent();
    }

    @Override
    public String getPath() {
        return path;
    }
}
