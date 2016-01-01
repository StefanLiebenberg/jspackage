package org.slieb.jspackage.compile;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slieb.jspackage.compile.nodes.SingleCompileNode;
import org.slieb.jspackage.compile.providers.SingleCompileResourceProvider;
import org.slieb.jspackage.compile.tasks.SingleCompileTask;
import slieb.kute.api.Resource;
import slieb.kute.providers.ChecksumCachedMapProvider;

@Singleton
public class CompilesProviderFactory {

    private final Provider<SingleCompileTask> singleCompileTaskProvider;

    @Inject
    public CompilesProviderFactory(Provider<SingleCompileTask> singleCompileTaskProvider) {
        this.singleCompileTaskProvider = singleCompileTaskProvider;
    }

    public SingleCompileResourceProvider createSingeCompileProvider(final SingleCompileNode node) {
        return new SingleCompileResourceProvider(singleCompileTaskProvider.get(), node);
    }

    public Resource.Provider createCachedSingleCompileProvider(SingleCompileNode node) {
        return new ChecksumCachedMapProvider(createSingeCompileProvider(node), node.getSourcesProvider());
    }

}
