package org.slieb.jspackage.service.resources;


import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.closure.javascript.printer.ComponentTestFileBuilder;
import org.slieb.dependencies.DependencyCalculator;
import slieb.kute.api.Resource;

public class ComponentTestResource extends AbstractHtmlResource {

    public final Resource.Readable testResource;

    public final DependencyCalculator<Resource.Readable, GoogDependencyNode<Resource.Readable>> calculator;

    private final String path;

    public ComponentTestResource(Readable testResource, DependencyCalculator<Readable, GoogDependencyNode<Readable>> calculator, String path) {
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
