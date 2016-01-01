package org.slieb.jspackage.compile.resources;


import com.google.javascript.jscomp.Compiler;
import slieb.kute.resources.ContentResource;

import java.io.IOException;

public class CompiledResource implements ContentResource {

    private final String path;

    private final Compiler compiler;

    public CompiledResource(String path, Compiler compiler) {
        this.path = path;
        this.compiler = compiler;
    }

    @Override
    public String getContent() throws IOException {
        return compiler.toSource();
    }

    @Override
    public String getPath() {
        return path;
    }
}
