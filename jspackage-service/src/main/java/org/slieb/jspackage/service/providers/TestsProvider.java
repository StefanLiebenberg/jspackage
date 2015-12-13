package org.slieb.jspackage.service.providers;


import com.google.common.collect.ImmutableList;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.util.stream.Stream;

/**
 *
 */
public class TestsProvider extends AbstractStreamsProvider<Resource.Readable> {

    private final ResourceProvider<Resource.Readable> testsProvider;

    public TestsProvider(ResourceProvider<Resource.Readable> sources,
                         ResourceProvider<Resource.Readable> toolsProvider) {
        this.testsProvider = new ComponentTestsProvider(
                new GroupResourceProvider<>(ImmutableList.of(sources, toolsProvider)));
    }


    @Override
    protected Stream<Stream<Resource.Readable>> streams() {
        return Stream.of(getTestsStream(), Stream.of(getAllTestsResource()));
    }


    public Stream<Resource.Readable> getTestsStream() {
        return testsProvider.stream();
    }

    public Resource.Readable getAllTestsResource() {
        return Kute.stringResource("/sources/all_tests.html", "<b>IMPLEMENT</b>");
    }
}
