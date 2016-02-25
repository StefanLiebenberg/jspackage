package org.slieb.jspackage.compile.resources;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.JSModule;
import org.slieb.kute.resources.ContentResource;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class JSModuleResource implements ContentResource, Serializable {

    private final Compiler compiler;
    private final JSModule jsModule;

    public JSModuleResource(Compiler compiler,
                            JSModule jsModule) {
        this.compiler = compiler;
        this.jsModule = jsModule;
    }

    @Override
    public String getContent() throws IOException {
        return compiler.toSource(jsModule);
    }

    @Override
    public String getPath() {
        return jsModule.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof JSModuleResource)) { return false; }
        JSModuleResource that = (JSModuleResource) o;
        return Objects.equals(compiler, that.compiler) &&
                Objects.equals(jsModule, that.jsModule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compiler, jsModule);
    }

    @Override
    public String toString() {
        return "JSModuleResource{" +
                "compiler=" + compiler +
                ", jsModule=" + jsModule +
                '}';
    }
}
