package org.slieb.jspackage.service.resources;


import com.google.common.collect.ImmutableMap;
import org.slieb.closure.javascript.internal.JsonPrinter;
import slieb.kute.api.Resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;

public class CssRenameMapResource implements Resource.Readable {

    private final String path;

    private final Optional<Map<String, String>> substitutionMap;

    public CssRenameMapResource(String path, Optional<Map<String, String>> substitutionMap) {
        this.path = path;
        this.substitutionMap = substitutionMap;
    }

    @Override
    public String getPath() {
        return path;
    }


    @Override
    public Reader getReader() throws IOException {
        return new StringReader("var css = " + new JsonPrinter().printObjectMap(getMap()) + ";\n");
    }

    private Map<String, String> getMap() {
        return this.substitutionMap.orElse(ImmutableMap.of());
    }
}
