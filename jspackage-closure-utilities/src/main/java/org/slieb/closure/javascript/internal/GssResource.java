package org.slieb.closure.javascript.internal;

import org.slieb.dependencies.DependencyCalculator;
import org.slieb.dependencies.DependencyNode;
import slieb.kute.api.Resource;
import slieb.kute.resources.ContentResource;
import slieb.kute.KuteIO;

import java.io.Serializable;
import java.util.Objects;

public class GssResource implements ContentResource, Serializable {

    private final String path;

    private final String namespace;

    private final DependencyCalculator<Resource.Readable, DependencyNode<Resource.Readable>> calculator;

    public GssResource(String path,
                       String namespace,
                       DependencyCalculator<Resource.Readable, DependencyNode<Resource.Readable>> calculator) {
        this.path = path;
        this.namespace = namespace;
        this.calculator = calculator;
    }


    @Override
    public String getContent() {
        return calculator.getResourcesFor(namespace).stream().map(KuteIO::readResourceUnsafe)
                .reduce("", (s, s2) -> s + "\n" + s2);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "GssResource{" +
                "path='" + path + '\'' +
                ", namespace='" + namespace + '\'' +
                ", calculator=" + calculator +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GssResource)) return false;
        GssResource that = (GssResource) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(namespace, that.namespace) &&
                Objects.equals(calculator, that.calculator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, namespace, calculator);
    }
}
