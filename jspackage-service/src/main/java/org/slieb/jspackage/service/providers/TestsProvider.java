package org.slieb.jspackage.service.providers;


import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.util.stream.Stream;

/**
 *
 */
public class TestsProvider extends AbstractStreamsProvider {

    private final ToolsProvider toolsProvider;

    private final ResourceProvider<? extends Resource.Readable> testsProvider;

    public TestsProvider(ToolsProvider toolsProvider) {
        this.toolsProvider = toolsProvider;
        this.testsProvider = new ComponentTestsProvider(this.toolsProvider);
    }


    @Override
    protected Stream<Stream<? extends Resource.Readable>> streams() {
        return Stream.of(
                getToolsStream(),
                getTestsStream(),
                Stream.of(getAllTestsResource()));
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
}
