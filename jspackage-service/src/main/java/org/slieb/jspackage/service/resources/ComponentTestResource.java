package org.slieb.jspackage.service.resources;

import org.slieb.closure.javascript.internal.ComponentTestFileBuilder;
import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.kute.api.Resource;
import org.slieb.kute.resources.ContentResource;

import java.io.Serializable;
import java.util.Objects;

public class ComponentTestResource implements ContentResource, Serializable {

    private final String path;

    private final Resource.Readable testResource;

    private final GoogDependencyCalculator calculator;

    public ComponentTestResource(String path,
                                 Readable testResource,
                                 GoogDependencyCalculator calculator) {
        this.path = path;
        this.testResource = testResource;
        this.calculator = calculator;
    }

    @Override
    public String getContent() {
        return new ComponentTestFileBuilder(testResource, calculator).getContent();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ComponentTestResource)) { return false; }
        ComponentTestResource that = (ComponentTestResource) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(testResource, that.testResource) &&
                Objects.equals(calculator, that.calculator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, testResource, calculator);
    }

    @Override
    public String toString() {
        return "ComponentTestResource{" +
                "path='" + path + '\'' +
                ", testResource=" + testResource +
                ", calculator=" + calculator +
                "} " + super.toString();
    }
}
