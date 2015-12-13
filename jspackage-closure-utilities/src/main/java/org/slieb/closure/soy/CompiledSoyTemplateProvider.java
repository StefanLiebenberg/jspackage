package org.slieb.closure.soy;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.ResourcePredicates;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CompiledSoyTemplateProvider implements ResourceProvider<Resource.Readable> {

    private final ResourceProvider<? extends Resource.Readable> sources, filteredSoyFiles;

    public CompiledSoyTemplateProvider(ResourceProvider<? extends Resource.Readable> sources) {
        this.sources = sources;
        this.filteredSoyFiles = Kute.filterResources(sources, ResourcePredicates.extensionFilter(".soy"));
    }

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {
        return Kute.findResource(stream(), path);
    }

    @Override
    public Stream<Resource.Readable> stream() {
        SoyJsSrcOptions jsSrcOptions = new SoyJsSrcOptions();
        List<Resource.Readable> readables = sources.stream().collect(toList());
        SoyFileSet.Builder builder = SoyFileSet.builder();
        readables.forEach(r -> builder.add(Kute.readResourceUnsafe(r), r.getPath()));
        List<String> paths = readables.stream().map(Resource::getPath).collect(toList());
        List<String> compiled = builder.build().compileToJsSrc(jsSrcOptions, null);
        Stream.Builder<Resource.Readable> stream = Stream.builder();
        for (int i = 0; i < paths.size(); i++) {
            stream.accept(Kute.stringResource(paths.get(i) + ".js", compiled.get(i)));
        }
        return stream.build();
    }
}
