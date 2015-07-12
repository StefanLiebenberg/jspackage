package org.slieb.jspackage.compile.resources;

import com.google.javascript.jscomp.SourceMap;
import slieb.kute.api.Resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


public class SourceMapResource implements Resource.Readable {
    private final String path;
    private final SourceMap sourceMap;

    public SourceMapResource(String path, SourceMap sourceMap) {
        this.path = path;
        this.sourceMap = sourceMap;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(sourceMap.toString());
    }
}
