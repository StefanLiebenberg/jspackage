package org.slieb.closure.soy;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import org.slieb.kute.Kute;
import org.slieb.kute.KuteIO;
import org.slieb.kute.KutePredicates;
import org.slieb.kute.api.Resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.slieb.throwables.ConsumerWithThrowable.castConsumerWithThrowable;

public class CompiledSoyTemplateProvider implements Resource.Provider {

    private final Resource.Provider sources, filteredSoyFiles;

    public CompiledSoyTemplateProvider(Resource.Provider sources) {
        this.sources = sources;
        this.filteredSoyFiles = Kute.filterResources(sources, KutePredicates.extensionFilter(".soy"));
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
        readables.forEach(castConsumerWithThrowable(r -> builder.add(KuteIO.readResource(r), r.getPath())));
        List<String> paths = readables.stream().map(Resource::getPath).collect(toList());
        List<String> compiled = builder.build().compileToJsSrc(jsSrcOptions, null);
        Stream.Builder<Resource.Readable> stream = Stream.builder();
        for (int i = 0; i < paths.size(); i++) {
            stream.accept(Kute.stringResource(paths.get(i) + ".js", compiled.get(i)));
        }
        return stream.build();
    }
}
