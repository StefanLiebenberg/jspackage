package org.slieb.jspackage.service.resources;

import org.slieb.closure.javascript.internal.DepsFileBuilder;
import org.slieb.kute.Kute;
import org.slieb.kute.KutePredicates;
import org.slieb.kute.api.Resource;

import java.io.*;

public class DepsResource implements Resource.Readable {

    private final String path;
    private final Resource.Provider filtered;

    public DepsResource(final String path,
                        final Resource.Provider provider) {
        this.path = path;
        this.filtered = Kute.filterResources(provider, KutePredicates.extensionFilter(".js"));
    }

    public String getContent() {
        return new DepsFileBuilder(filtered).getDependencyContent();
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(getContent());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(getContent().getBytes());
    }

    @Override
    public String getPath() {
        return path;
    }
}
