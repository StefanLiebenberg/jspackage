package org.slieb.jspackage.service.resources;

import com.google.common.collect.ImmutableMap;
import org.slieb.closure.javascript.internal.JsonPrinter;
import org.slieb.kute.resources.ContentResource;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toMap;


public class DefinesResource implements ContentResource {


    private final String path;
    private final Optional<Properties> properties;

    private final JsonPrinter jsonPrinter = new JsonPrinter();

    public DefinesResource(String path,
                           Optional<Properties> properties) {
        this.path = checkNotNull(path, "path cannot be notnull");
        this.properties = checkNotNull(properties, "properties cannot be notnull");
    }

    public Map<String, String> getPropertiesMap() {
        return properties
                .map(p -> p.entrySet().stream()
                        .collect(toMap(
                                e -> String.valueOf(e.getKey()),
                                e -> String.valueOf(e.getValue()))))
                .orElse(ImmutableMap.of());
    }

    @Override
    public String getContent() {
        return "var defines = " + jsonPrinter.printStringMap(getPropertiesMap()) + ";\n";
    }

    @Override
    public String getPath() {
        return path;
    }
}
