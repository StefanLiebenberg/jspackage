package org.slieb.jspackage.compile.resources;

import com.google.javascript.jscomp.Result;
import slieb.kute.api.Resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ResultResource implements Resource.Readable {

    private final String path;

    private final Result result;

    public ResultResource(String path, Result result) {
        this.path = path;
        this.result = result;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader("RESULT: ...");
    }
}
