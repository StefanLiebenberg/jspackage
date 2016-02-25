package org.slieb.jspackage.compile.resources;

import com.google.javascript.jscomp.Result;
import org.slieb.kute.resources.ContentResource;

import java.io.IOException;

public class DebugResource implements ContentResource {

    private final String path;

    private final Result result;

    public DebugResource(String path,
                         Result result) {
        this.path = path;
        this.result = result;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getContent() throws IOException {
        return result.debugLog;
    }
}
