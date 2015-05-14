package org.slieb.jspackage.service.providers;

import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.stream.Stream;

/**
 * The plan:
 * <p>
 * Sources
 * - al raw files on classpath
 * - .html, .js, .images, .css, .gss
 * <p>
 * Tools
 * - fallback to sources. ( requires sources )
 * - To js compiled soy ( requires sources )
 * - To css compiled gss files. (requires sources:css )
 * - Dep.js and defines.js ( requires soy )
 * <p>
 * Testing
 * - .html files for _test.js files ( requires tools:soy )
 * - all_tests.html file ( requires sources, tests )
 */
public class ServiceProvider extends AbstractStreamsProvider {

    private final ResourceProvider<? extends Resource.Readable> sources;

    private final ToolsProvider toolsProvider;

    private final TestsProvider testsProvider;

    private final IndexProvider indexer;

    public ServiceProvider(ResourceProvider<? extends Resource.Readable> sources) {
        this.sources = sources;
        this.toolsProvider = new ToolsProvider(this.sources);
        this.testsProvider = new TestsProvider(this.toolsProvider);
        this.indexer = new IndexProvider(this.testsProvider);
    }

    @Override
    protected Stream<Stream<? extends Resource.Readable>> streams() {
        return Stream.of(testsProvider.stream(), indexer.stream());
    }

    public void clear() {
        this.indexer.clear();
    }

}
