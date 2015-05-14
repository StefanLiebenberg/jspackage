package org.slieb.jspackage.service.providers;


import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.closure.javascript.providers.CompiledSoyProvider;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This resource provider will compose the sources.
 * <p>
 * Tools
 * - fallback to sources. ( requires sources )
 * - To js compiled soy ( requires sources )
 * - To css compiled gss files. (requires sources:css )
 * - Dep.js and defines.js ( requires soy )
 */
public class ToolsProvider extends AbstractStreamsProvider {

    private final ResourceProvider<? extends Resource.Readable> sourceProvider;

    private final CompiledSoyProvider soyProvider;

    private final GoogDependencyCalculator calculator;

    private final ExternsProvider externsProvider;

    public ToolsProvider(ResourceProvider<? extends Resource.Readable> sourceProvider) {
        this.sourceProvider = sourceProvider;
        this.soyProvider = new CompiledSoyProvider(this.sourceProvider);
        this.externsProvider = new ExternsProvider();
        this.calculator = GoogResources.getCalculator(this);
    }


    public List<? extends Resource.Readable> getResourcesForNamespaceSet(Set<String> namespaces) {
        return calculator.getResourcesFor(namespaces);
    }

    public List<? extends Resource.Readable> getExterns() {
        return externsProvider.stream().collect(Collectors.toList());
    }

    @Override
    protected Stream<Stream<? extends Resource.Readable>> streams() {
        return Stream.of(sourceStream(), toolsStream());
    }

    public Stream<? extends Resource.Readable> sourceStream() {
        return sourceProvider.stream();
    }

    public Stream<Resource.Readable> toolsStream() {
        return Stream
                .of(compiledSoyStream(), compiledCssStream(),
                        Stream.of(getDepsResource(), getDefinesFile(), getCssRenameMap()))
                .flatMap(s -> s);
    }

    public Stream<? extends Resource.Readable> compiledSoyStream() {
        return soyProvider.stream();
    }

    public Stream<Resource.Readable> compiledCssStream() {
        return Stream.empty();
    }


    public Resource.Readable getDepsResource() {
        return Resources.stringResource("/deps.js", "");
    }

    public Resource.Readable getCssRenameMap() {
        return Resources.stringResource("/cssRenameMap.js", "");
    }

    public Resource.Readable getDefinesFile() {
        return Resources.stringResource("/defines.js", "");
    }


}
