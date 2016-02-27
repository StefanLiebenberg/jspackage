package org.slieb.jspackage.container;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;
import org.slieb.kute.providers.RenamedNamespaceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static org.slieb.kute.KuteIO.readResource;
import static org.slieb.kute.KutePredicates.extensionFilter;
import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;

public class StandardLayoutDeployContainer implements DeployContainer {

    private final Resource.Provider assetsProvider, templatesProvider, informationProvider;

    private final Supplier<SoyFileSet.Builder> soyBuilderSupplier;

    public StandardLayoutDeployContainer(final Resource.Provider resourceProvider,
                                         final Supplier<SoyFileSet.Builder> soyBuilderSupplier) {
        this.informationProvider = new RenamedNamespaceProvider(resourceProvider, "/information/", "/");
        this.assetsProvider = new RenamedNamespaceProvider(resourceProvider, "/assets/", "/");
        this.templatesProvider = Kute.filterResources(new RenamedNamespaceProvider(resourceProvider, "/templates/", "/"), extensionFilter(".soy"));
        this.soyBuilderSupplier = soyBuilderSupplier;
    }

    public StandardLayoutDeployContainer(final Resource.Provider resourceProvider) {
        this(resourceProvider, SoyFileSet::builder);
    }

    @Override
    public Optional<InputStream> getResource(final String path) throws IOException {
        return assetsProvider
                .getResourceByName(path)
                .flatMap(castFunctionWithThrowable(Resource.Readable::getInputStream)
                                 .withLogging()
                                 .thatReturnsOptional());
    }

    @Override
    public SoyTofu getTofu() throws IOException {
        final SoyFileSet.Builder builder = soyBuilderSupplier.get();
        templatesProvider.forEach(resource -> builder.add(readResource(resource), resource.getPath()));
        return builder.build().compileToTofu();
    }

    @Override
    public Optional<InputStream> getInformationResource(final String path) throws IOException {
        return informationProvider
                .getResourceByName(path)
                .flatMap(castFunctionWithThrowable(Resource.Readable::getInputStream)
                                 .withLogging()
                                 .thatReturnsOptional());
    }
}
