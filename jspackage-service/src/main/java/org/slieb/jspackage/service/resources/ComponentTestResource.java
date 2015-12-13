package org.slieb.jspackage.service.resources;


import org.slieb.closure.dependencies.*;
import org.slieb.closure.javascript.internal.ComponentTestFileBuilder;
import slieb.kute.api.Resource;

public class ComponentTestResource extends AbstractHtmlResource {

    public final Resource.Readable testResource;

    public final GoogDependencyCalculator calculator;

    private final String path;

    public ComponentTestResource(Readable testResource, GoogDependencyCalculator calculator, String path) {
        this.testResource = testResource;
        this.calculator = calculator;
        this.path = path;
    }

    public String getHtmlContent() {
        return new ComponentTestFileBuilder(testResource, calculator).getContent();
    }


    @Override
    public String getPath() {
        return path;
    }
}
