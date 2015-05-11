package org.slieb.jspackage.service.providers;


import com.google.common.collect.ImmutableList;
import org.slieb.closure.javascript.GoogDependencyNode;
import org.slieb.closure.javascript.GoogResources;
import org.slieb.closure.javascript.providers.CompiledSoyProvider;
import org.slieb.dependencies.DependencyCalculator;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.util.stream.Stream;

import static slieb.kute.resources.Resources.findFirstResource;
import static slieb.kute.resources.Resources.findResource;

/**
 * This resource provider will compose the sources.
 * <p>
 * Tools
 * - fallback to sources. ( requires sources )
 * - To js compiled soy ( requires sources )
 * - To css compiled gss files. (requires sources:css )
 * - Dep.js and defines.js ( requires soy )
 */
public class ToolsProvider implements ResourceProvider<Resource.Readable> {

    private final ResourceProvider<? extends Resource.Readable> sourceProvider;

    private final CompiledSoyProvider soyProvider;

    private final DependencyCalculator<? extends Resource.Readable, GoogDependencyNode<Resource.Readable>> calculator;

    public ToolsProvider(ResourceProvider<? extends Resource.Readable> sourceProvider) {
        this.sourceProvider = sourceProvider;
        this.soyProvider = new CompiledSoyProvider(this.sourceProvider);
        this.calculator = GoogResources.getCalculator(this);
    }

    public Stream<? extends Resource.Readable> compiledSoyStream() {
        return soyProvider.stream();
    }

    public Stream<Resource.Readable> compiledCssStream() {
        return Stream.of();
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

    public Stream<Resource.Readable> toolsStream() {
        return Stream.of(getDepsResource(), getDefinesFile(), getCssRenameMap());
    }

    private Stream<Stream<? extends Resource.Readable>> streams() {
        return ImmutableList.of(
                sourceProvider.stream(),
                compiledSoyStream(),
                compiledCssStream(),
                toolsStream()).stream();
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return streams().flatMap(f -> f);
    }

    @Override
    public Resource.Readable getResourceByName(String path) {
        return findFirstResource(streams().map(s -> findResource(s, path)));
    }
}
