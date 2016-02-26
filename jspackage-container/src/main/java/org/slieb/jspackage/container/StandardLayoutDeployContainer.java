package org.slieb.jspackage.container;

import com.google.gson.Gson;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;
import org.slieb.kute.providers.RenamedNamespaceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.slieb.kute.KuteIO.readResource;
import static org.slieb.kute.KutePredicates.extensionFilter;
import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;

public class StandardLayoutDeployContainer implements DeployContainer {

    private final Resource.Provider assetsProvider, templatesProvider, informationProvider;

    private final Supplier<SoyFileSet.Builder> soyBuilderSupplier;

    private final Gson gson;

    public StandardLayoutDeployContainer(final Resource.Provider resourceProvider,
                                         final Supplier<SoyFileSet.Builder> soyBuilderSupplier) {
        this.informationProvider = new RenamedNamespaceProvider(resourceProvider, "/information/", "/");
        this.assetsProvider = new RenamedNamespaceProvider(resourceProvider, "/assets/", "/");
        this.templatesProvider = Kute.filterResources(new RenamedNamespaceProvider(resourceProvider, "/templates/", "/"), extensionFilter(".soy"));
        this.soyBuilderSupplier = soyBuilderSupplier;
        this.gson = new Gson();
    }

    @Override
    public Optional<InputStream> getAssetInputStream(final String path) throws IOException {
        return assetsProvider
                .getResourceByName(path)
                .flatMap(castFunctionWithThrowable(Resource.Readable::getInputStream)
                                 .withLogging()
                                 .thatReturnsOptional());
    }

    @Override
    public Optional<SoyTofu> getTofu() throws IOException {
        if (templatesProvider.stream().findAny().isPresent()) {
            final SoyFileSet.Builder builder = soyBuilderSupplier.get();
            templatesProvider.forEach(resource -> builder.add(readResource(resource), resource.getPath()));
            return Optional.of(builder.build().compileToTofu());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Map<String, Object>> getInformation(final String configurationName) {
        return informationProvider
                .getResourceByName(configurationName)
                .flatMap(castFunctionWithThrowable(this::getInformationMapFromResource)
                                 .withLogging()
                                 .thatReturnsOptional());
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getInformationMapFromResource(final Resource.Readable resource) throws IOException {
        try (Reader reader = resource.getReader()) {
            return (Map<String, Object>) gson.fromJson(reader, HashMap.class);
        }
    }
}
