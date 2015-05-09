package org.slieb.closure.javascript.internal;


import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.dependencies.DependencyCalculator;
import slieb.kute.api.Resource;

public class ComponentTestFileBuilder {

    private final Resource.Readable testResource;

    private final DependencyCalculator<Resource.Readable, GoogDependencyNode<Resource.Readable>> calculator;

    public ComponentTestFileBuilder(Resource.Readable testResource, DependencyCalculator<Resource.Readable, GoogDependencyNode<Resource.Readable>> calculator) {
        this.testResource = testResource;
        this.calculator = calculator;
    }

    public String getContent() {
        StringBuilder builder = new StringBuilder()
                .append("<!DOCTYPE html><html><head>");
        calculator.getResourcesFor(testResource)
                .stream()
                .map(this::getResourceIndex)
                .forEach(builder::append);
        return builder
                .append("</head></html>")
                .toString();
    }


    private String getResourceIndex(Resource.Readable readable) {
        return String.format("<script src='%s'></script>", readable.getPath());
    }
}
