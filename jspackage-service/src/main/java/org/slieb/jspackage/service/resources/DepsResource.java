package org.slieb.jspackage.service.resources;

import org.slieb.closure.javascript.internal.DepsFileBuilder;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static slieb.kute.resources.ResourcePredicates.extensionFilter;


public class DepsResource implements Resource.Readable {


    private final String path;
    private final ResourceProvider<? extends Resource.Readable> filtered;


    public DepsResource(String path,
                        ResourceProvider<? extends Readable> provider) {
        this.path = path;
        this.filtered = Kute.filterResources(provider, extensionFilter(".js"));
    }


    public String getContent() {
        return new DepsFileBuilder(filtered).getDependencyContent();
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(getContent());
    }

    @Override
    public String getPath() {
        return path;
    }

}
