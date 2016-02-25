package org.slieb.jspackage.compile.resources;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.SourceMap;
import org.slieb.kute.resources.ContentResource;

import java.io.IOException;

public class SourceMapResource implements ContentResource {

    private final String path;
    private final SourceMap sourceMap;

    public SourceMapResource(String path,
                             SourceMap sourceMap) {
        this.path = path;
        this.sourceMap = sourceMap;
    }

    public SourceMapResource(String path,
                             Compiler compiler) {
        this(path, compiler.getSourceMap());
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getContent() throws IOException {
        return sourceMap.toString();
    }
}
