package org.slieb.closure.javascript.internal;


import org.slieb.closure.dependencies.GoogDependencyCalculator;
import slieb.kute.api.Resource;

public class ComponentTestFileBuilder {

    private final Resource.Readable testResource;

    private final GoogDependencyCalculator calculator;

    public ComponentTestFileBuilder(Resource.Readable testResource,
                                    GoogDependencyCalculator calculator) {
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
