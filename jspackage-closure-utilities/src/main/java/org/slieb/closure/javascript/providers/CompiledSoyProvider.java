package org.slieb.closure.javascript.providers;

import com.google.common.io.CharSource;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import slieb.kute.KuteLambdas;
import slieb.kute.api.Resource;

import java.io.*;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * An expensive provider.
 */
public class CompiledSoyProvider implements Resource.Provider {

    private final Predicate<Resource> SOY_FILTER = KuteLambdas.extensionFilter(".soy");

    /**
     * The source files.
     */
    private final Resource.Provider provider;

    private final SoyJsSrcOptions jsSrcOptions;


    public CompiledSoyProvider(Resource.Provider provider,
                               SoyJsSrcOptions options) {
        this.provider = provider;
        this.jsSrcOptions = options;
    }

    public CompiledSoyProvider(Resource.Provider provider) {
        this(provider, new SoyJsSrcOptions());
        this.jsSrcOptions.setGoogMsgsAreExternal(true);
        this.jsSrcOptions.setShouldDeclareTopLevelNamespaces(true);
        this.jsSrcOptions.setShouldGenerateGoogMsgDefs(true);
        this.jsSrcOptions.setBidiGlobalDir(1);
        this.jsSrcOptions.setShouldProvideRequireJsFunctions(true);
    }


    @Override
    public Stream<Resource.Readable> stream() {
        return soyStream().map(this::liveSoyFileResource);
    }

    private Stream<? extends Resource.Readable> soyStream() {
        return provider.stream().filter(SOY_FILTER);
    }

    private LiveSoyFileResource liveSoyFileResource(Resource.Readable resource) {
        String path = getJSNameFromSoyName(resource.getPath());
        return new LiveSoyFileResource(path, resource, jsSrcOptions, null);
    }

    private String getSoyNameFromJavascript(String jsName) {
        return jsName.replace(".soy.js", ".soy");
    }

    private String getJSNameFromSoyName(String soyName) {
        return soyName + ".js";
    }


    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        return provider.getResourceByName(getSoyNameFromJavascript(path)).filter(SOY_FILTER)
                .map(this::liveSoyFileResource);
    }
}


class LiveSoyFileResource implements Resource.Readable {

    private final Resource.Readable sourceResource;

    private final SoyJsSrcOptions options;

    private final SoyMsgBundle bundle;

    private final SoyFileSet sfs;

    public LiveSoyFileResource(String path,
                               Readable sourceResource,
                               SoyJsSrcOptions options,
                               SoyMsgBundle bundle) {
        this.sourceResource = sourceResource;
        this.options = options;
        this.bundle = bundle;
        this.sfs = getSingleFileSet(sourceResource);
    }

    @Override
    public String getPath() {
        return sourceResource.getPath();
    }

    @Override
    public StringReader getReader() throws IOException {
        return new StringReader(getContent());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(getContent().getBytes());
    }

    private String getContent() {
        return sfs.compileToJsSrc(options, bundle).get(0);
    }

    private static SoyFileSet getSingleFileSet(Resource.Readable readable) {
        SoyFileSet.Builder sfsBuilder = SoyFileSet.builder();
        sfsBuilder.add(new ResourceCharSource(readable), readable.getPath());
        return sfsBuilder.build();
    }
}

class ResourceCharSource extends CharSource {

    private final Resource.Readable readable;

    public ResourceCharSource(Resource.Readable readable) {
        this.readable = readable;
    }

    public Reader openStream() throws IOException {
        return readable.getReader();
    }
}


