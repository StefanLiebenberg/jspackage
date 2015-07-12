package org.slieb.jspackage.compile.resources;


import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.JSModule;
import slieb.kute.api.Resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class CompiledJSModuleResource implements Resource.Readable {

    private final String path;

    private final Compiler compiler;

    private final JSModule jsModule;

    public CompiledJSModuleResource(String path, Compiler compiler, JSModule jsModule) {
        this.path = path;
        this.compiler = compiler;
        this.jsModule = jsModule;
    }

    public String getPath() {
        return path;
    }

    public String getCompiledOutput() {
        return compiler.toSource(jsModule);
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(getCompiledOutput());
    }
}
