package org.slieb.jspackage.container;

import com.google.gson.Gson;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;
import org.slieb.kute.providers.RenamedNamespaceProvider;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;
import static org.slieb.kute.KuteIO.readResource;
import static org.slieb.kute.KutePredicates.extensionFilter;
import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;

public class StandardLayoutDeployContainer implements DeployContainer {

    private final Resource.Provider resourceProvider;

    private final Supplier<SoyFileSet.Builder> soyBuilderProvider;

    private final Gson gson;

    public StandardLayoutDeployContainer(final Resource.Provider resourceProvider,
                                         final Supplier<SoyFileSet.Builder> soyBuilderProvider) {
        this.resourceProvider = resourceProvider;
        this.soyBuilderProvider = soyBuilderProvider;
        this.gson = new Gson();
    }

    @Override
    public Resource.Provider getAssetsProvider() {
        return new RenamedNamespaceProvider(resourceProvider, "/assets/", "/");
    }

    protected Resource.Provider getTemplateProvider() {
        return Kute.filterResources(new RenamedNamespaceProvider(resourceProvider, "/templates/", "/"), extensionFilter(".soy"));
    }

    protected Resource.Provider getInformationProvider() {
        return new RenamedNamespaceProvider(resourceProvider, "/information/", "/");
    }

    public SoyTofu getTofu() throws IOException {
        final SoyFileSet.Builder builder = soyBuilderProvider.get();
        getTemplateProvider().forEach(resource -> builder.add(readResource(resource), resource.getPath()));
        return builder.build().compileToTofu();
    }

    @Override
    public Map<String, Object> getInformation(final String configurationName) {
        return getInformationProvider()
                .getResourceByName(configurationName)
                .flatMap(castFunctionWithThrowable(this::getInformationMapFromResource).thatReturnsOptional())
                .orElse(emptyMap());
    }

    protected Map<String, Object> getInformationMapFromResource(final Resource.Readable resource) throws IOException {
        try (Reader reader = resource.getReader()) {
            //noinspection unchecked
            return (Map<String, Object>) gson.fromJson(reader, HashMap.class);
        }
    }
}
