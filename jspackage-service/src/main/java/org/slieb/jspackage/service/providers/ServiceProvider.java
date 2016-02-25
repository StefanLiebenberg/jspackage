package org.slieb.jspackage.service.providers;

import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;

import java.util.stream.Stream;

/**
 * The plan:
 *
 * <p>
 * Sources
 * - al raw files on classpath
 * - .html, .js, .images, .css, .gss
 * <p>
 * Tools
 * - To js compiled soy ( requires sources )
 * - To css compiled gss files. (requires sources:css )
 * - Dep.js and defines.js ( requires soy )
 * <p>
 * Testing
 * - .html files for _test.js files ( requires tools:soy )
 * - all_tests.html file ( requires sources, tests )
 */
public class ServiceProvider extends AbstractStreamsProvider {

    private final Resource.Provider sources;
    private final BuildProvider toolsProvider;
    private final TestsProvider testsProvider;
    private final IndexProvider indexProvider;

    public ServiceProvider(Resource.Provider sources) {
        this.sources = sources;
        this.toolsProvider = new BuildProvider(sources);
        this.testsProvider = new TestsProvider(this.sources, this.toolsProvider);
        this.indexProvider = new IndexProvider(Kute.group(this.sources, this.toolsProvider, this.testsProvider));
    }

    @Override
    protected Stream<Stream<Resource.Readable>> streams() {
        return Stream.of(sources.stream(), toolsProvider.stream(), testsProvider.stream(), this.indexProvider.stream());
    }


}
