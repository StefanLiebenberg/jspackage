package org.slieb.jspackage.service.providers;


import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.javascript.jscomp.CommandLineRunner;
import org.apache.commons.io.IOUtils;
import slieb.kute.Kute;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.implementations.StringSupplierResource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExternsProvider implements ResourceProvider<StringSupplierResource> {

    private final ConcurrentHashMap<String, String> map;

    public ExternsProvider() {
        this.map = new ConcurrentHashMap<>();
    }

    private InputStream getExternsZipInputStream() {
        InputStream input = CommandLineRunner.class.getResourceAsStream("/externs.zip");
        if (input == null) {
            // In some environments, the externs.zip is relative to this class.
            input = CommandLineRunner.class.getResourceAsStream("externs.zip");
        }
        Preconditions.checkNotNull(input);
        return input;
    }

    private void buildExterns() {
        if (map.isEmpty()) {
            try (InputStream input = getExternsZipInputStream(); ZipInputStream zip = new ZipInputStream(input)) {
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {
                    BufferedInputStream entryStream = new BufferedInputStream(ByteStreams.limit(zip, entry.getSize()));
                    map.put(entry.getName(), IOUtils.toString(entryStream, "UTF-8"));
                    entry = zip.getNextEntry();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Stream<StringSupplierResource> stream() {
        buildExterns();
        return map.entrySet().stream().map(e -> Kute.stringResource(e.getKey(), e.getValue()));
    }

    @Override
    public Optional<StringSupplierResource> getResourceByName(String path) {
        buildExterns();
        if (map.containsKey(path)) {
            return Optional.of(Kute.stringResource(path, map.get(path)));
        } else {
            return Optional.empty();
        }
    }
}
