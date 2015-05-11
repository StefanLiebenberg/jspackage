package org.slieb.jspackage.service.providers;


import com.google.common.collect.ImmutableList;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.util.stream.Stream;

import static slieb.kute.resources.Resources.findFirstResource;
import static slieb.kute.resources.Resources.findResource;

/**
 *
 */
public class TestsProvider implements ResourceProvider<Resource.Readable> {

    private final ToolsProvider toolsProvider;

    private final ResourceProvider<? extends Resource.Readable> testsProvider;

    public TestsProvider(ToolsProvider toolsProvider) {
        this.toolsProvider = toolsProvider;
        this.testsProvider = new ComponentTestsProvider(this.toolsProvider);
    }

    public Stream<? extends Resource.Readable> getTestsStream() {
        return testsProvider.stream();
    }

    public Stream<? extends Resource.Readable> getToolsStream() {
        return toolsProvider.stream();
    }

    public Resource.Readable getAllTestsResource() {
        return Resources.stringResource("", "/all_tests.html");
    }


    private Stream<Stream<? extends Resource.Readable>> streams() {
        return ImmutableList.of(
                getToolsStream(),
                getTestsStream(),
                Stream.of(getAllTestsResource())
        ).stream();
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return streams().flatMap(s -> s);
    }

    @Override
    public Resource.Readable getResourceByName(String path) {
        return findFirstResource(streams().map(s -> findResource(s, path)));
    }
}
