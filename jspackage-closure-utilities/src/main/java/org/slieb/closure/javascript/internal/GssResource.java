package org.slieb.closure.javascript.internal;

import org.slieb.dependencies.DependencyCalculator;
import org.slieb.dependencies.DependencyNode;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.resources.implementations.AbstractResource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class GssResource extends AbstractResource implements Resource.Readable {

    private final String namespace;

    private final DependencyCalculator<Resource.Readable, DependencyNode<Resource.Readable>> calculator;

    public GssResource(String path,
                       String namespace,
                       DependencyCalculator<Resource.Readable, DependencyNode<Resource.Readable>> calculator) {
        super(path);
        this.namespace = namespace;
        this.calculator = calculator;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(calculator.getResourcesFor(namespace).stream().map(Kute::readResourceUnsafe)
                                        .reduce("", (s, s2) -> s + "\n" + s2));
    }

}
