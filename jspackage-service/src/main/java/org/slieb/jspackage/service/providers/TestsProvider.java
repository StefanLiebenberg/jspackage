package org.slieb.jspackage.service.providers;


import slieb.kute.Kute;
import slieb.kute.api.Resource;

import java.util.stream.Stream;

/**
 *
 */
public class TestsProvider extends AbstractStreamsProvider {

    private final Resource.Provider testsProvider;

    public TestsProvider(Resource.Provider sources,
                         Resource.Provider toolsProvider) {
        this.testsProvider = new ComponentTestsProvider(Kute.group(sources, toolsProvider));
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
