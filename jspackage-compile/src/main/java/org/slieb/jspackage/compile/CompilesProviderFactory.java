package org.slieb.jspackage.compile;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slieb.jspackage.compile.nodes.SingleCompileNode;
import org.slieb.jspackage.compile.providers.SingleCompileResourceProvider;
import org.slieb.jspackage.compile.tasks.SingleCompileTask;
import org.slieb.kute.api.Resource;
import org.slieb.kute.providers.ChecksumCachedProvider;

@Singleton
public class CompilesProviderFactory {

    private final Provider<SingleCompileTask> singleCompileTaskProvider;

    @Inject
    public CompilesProviderFactory(Provider<SingleCompileTask> singleCompileTaskProvider) {
        this.singleCompileTaskProvider = singleCompileTaskProvider;
    }

    public Resource.Provider createSingeCompileProvider(final SingleCompileNode node) {
        return new SingleCompileResourceProvider(singleCompileTaskProvider.get(), node);
    }

    public Resource.Provider createCachedSingleCompileProvider(SingleCompileNode node) {
        return new ChecksumCachedProvider(node.getSourcesProvider(), () -> createSingeCompileProvider(node));
    }
}
