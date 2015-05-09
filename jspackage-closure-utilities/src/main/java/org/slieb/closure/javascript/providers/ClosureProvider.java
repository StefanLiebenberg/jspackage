package org.slieb.closure.javascript.providers;


import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.Supplier;

public class ClosureProvider {
    private ResourceProvider<Resource.Readable> providerProvider;

    private Resource.Readable getDependencyResource(String path, Supplier<String> stringSupplier) {
        return new Resource.Readable() {
            @Override
            public String getPath() {
                return path;
            }

            @Override
            public Reader getReader() throws IOException {
                return new StringReader(stringSupplier.get());
            }
        };
    }
}
