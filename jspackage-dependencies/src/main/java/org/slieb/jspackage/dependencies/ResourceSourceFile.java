package org.slieb.jspackage.dependencies;

import com.google.javascript.jscomp.SourceFile;
import org.slieb.kute.KuteIO;
import org.slieb.kute.api.Resource;

import java.io.IOException;

public class ResourceSourceFile extends SourceFile {

    private final Resource.Readable readable;

    private String cached;

    public ResourceSourceFile(Resource.Readable resource) {
        super(resource.getPath());
        this.readable = resource;
        this.cached = null;
    }

    @Override
    public synchronized String getCode() throws IOException {
        if (cached == null) {
            cached = KuteIO.readResource(readable);
        }
        return cached;
    }


    @Override
    public void clearCachedSource() {
        this.cached = null;
    }
}
