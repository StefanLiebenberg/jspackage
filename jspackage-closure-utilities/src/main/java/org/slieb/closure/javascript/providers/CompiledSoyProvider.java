package org.slieb.closure.javascript.providers;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;
import slieb.kute.resources.implementations.StringSupplierResource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static slieb.kute.resources.ResourcePredicates.extensionFilter;

/**
 * An expensive provider.
 */
public class CompiledSoyProvider implements ResourceProvider<StringSupplierResource> {

    private final Predicate<Resource> SOY_FILTER = extensionFilter(".soy");

    /**
     * The source files.
     */
    private final ResourceProvider<? extends Resource.Readable> provider;

    private final SoyJsSrcOptions jsSrcOptions;

    private final ConcurrentHashMap<String, StringSupplierResource> compiledMap;

    public CompiledSoyProvider(ResourceProvider<? extends Resource.Readable> provider, SoyJsSrcOptions options) {
        this.provider = provider;
        this.jsSrcOptions = options;
        this.compiledMap = new ConcurrentHashMap<>();
    }

    public CompiledSoyProvider(ResourceProvider<? extends Resource.Readable> provider) {
        this(provider, new SoyJsSrcOptions());
        this.jsSrcOptions.setGoogMsgsAreExternal(true);
        this.jsSrcOptions.setShouldDeclareTopLevelNamespaces(true);
        this.jsSrcOptions.setShouldGenerateGoogMsgDefs(true);
        this.jsSrcOptions.setBidiGlobalDir(1);
        this.jsSrcOptions.setShouldProvideRequireJsFunctions(true);
    }

    private Stream<StringSupplierResource> compiledStream() {
        if (provider.stream().filter(SOY_FILTER).limit(1).count() != 0) {
            // build a list of pairs. maintaining order of list.
            final List<Pair> pairs = provider.stream().filter(SOY_FILTER).map(Pair::fromResource).collect(toList());
            final SoyFileSet.Builder builder = SoyFileSet.builder();
            // add pairs in order to builder
            pairs.forEach(p -> builder.add(p.content, p.path));
            // get list of results, in order of pairs.
            List<String> result = builder.build().compileToJsSrc(jsSrcOptions, null);
            // rebuild resources and store to compiledMap.
            return IntStream.range(0, pairs.size()).boxed()
                    .map(i -> Resources.stringResource(pairs.get(i).path + ".js", result.get(i)));
        } else {
            return Stream.of();
        }
    }

    private void clear() {
        compiledMap.clear();
    }

    private void compileIfNeeded() {
        if (compiledMap.isEmpty()) {
            compiledStream().forEach(r -> compiledMap.put(r.getPath(), r));
        }
    }

    @Override
    public Stream<StringSupplierResource> stream() {
        compileIfNeeded();
        return compiledMap.values().stream();
    }

    @Override
    public StringSupplierResource getResourceByName(String path) {
        compileIfNeeded();
        if (compiledMap.containsKey(path)) {
            return compiledMap.get(path);
        }
        return null;
    }
}


class Pair {
    public final String path, content;

    public Pair(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public static Pair fromResource(Resource.Readable readable) {
        try {
            return new Pair(readable.getPath(), Resources.readResource(readable));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
