package org.slieb.jspackage.dependencies;

import com.google.javascript.jscomp.SourceFile;
import org.slieb.dependencies.DependenciesHelper;
import org.slieb.dependencies.DependencyParser;
import org.slieb.dependencies.ModuleResolver;
import org.slieb.kute.Kute;
import org.slieb.kute.KutePredicates;
import org.slieb.kute.api.Resource;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.slieb.dependencies.DependencyUtils.getDependencyMap;

public class GoogResources {

    public static GoogDependencyCalculator getCalculator(Resource.Provider resourceProvider,
                                                         DependencyParser<Resource.Readable, GoogDependencyNode>
                                                                 parser) {
        return new GoogDependencyCalculator(resourceProvider, parser);
    }

    public static GoogDependencyCalculator getCalculator(Resource.Provider resourceProvider) {

        return new GoogDependencyCalculator(resourceProvider);
    }

    public static GoogDependencyParser getDependencyParser() {
        return GoogDependencyCalculator.PARSER;
    }

    public static GoogDependencyNode parse(Resource.Readable resource) {
        return getDependencyParser().parse(resource);
    }

    public static Resource.Provider getResourceProviderForSourceDirectories(final Collection<File> directories) {
        return Kute.group(directories.stream().distinct().map(Kute::provideFrom).collect(toSet()));
    }

    public static Resource.Provider getResourceProviderForSourceDirectories(
            final Collection<File> directories,
            final String... extensions) {
        return Kute.filterResources(getResourceProviderForSourceDirectories(directories),
                                    KutePredicates.extensionFilter(extensions));
    }

    public static SourceFile getSourceFileFromResource(Resource.Readable readable) {
        try (Reader reader = readable.getReader()) {
            return SourceFile.fromReader(readable.getPath(), reader);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    public static ModuleResolver<GoogDependencyNode> getModuleResolver(Resource.Provider resourceProvider,
                                                                       String commonModule) {
        return getModuleResolver(resourceProvider, commonModule, getHelper());
    }

    public static ModuleResolver<GoogDependencyNode> getModuleResolver(final Resource.Provider resourceProvider,
                                                                       final String commonModule,
                                                                       final DependenciesHelper<GoogDependencyNode>
                                                                               helper) {
        final List<GoogDependencyNode> fullList = resourceProvider.stream().map(GoogResources::parse).collect(toList());
        return getModuleResolver(helper.getResolvableSet(fullList), commonModule, helper.getBaseList(fullList));
    }

    private static ModuleResolver<GoogDependencyNode> getModuleResolver(Set<GoogDependencyNode> resolvableSet,
                                                                        String commonModule,
                                                                        List<GoogDependencyNode> baseList) {
        return new ModuleResolver<>(getDependencyMap(resolvableSet), commonModule, baseList);
    }

    public static Map<GoogDependencyNode, Set<GoogDependencyNode>> createDependencyMap(final Resource.Provider
                                                                                               provider) {
        return getDependencyMap(provider.stream()
                                        .map(GoogResources::parse)
                                        .collect(toList()));
    }

    public static DependenciesHelper<GoogDependencyNode> getHelper() {
        return new GoogDependencyHelper();
    }

    public static DependenciesHelper<GoogDependencyNode> getHelper(final List<GoogDependencyNode> baseList) {
        final DependenciesHelper<GoogDependencyNode> defaultHelper = getHelper();
        return new DependenciesHelper<GoogDependencyNode>() {
            @Override
            public Set<GoogDependencyNode> getResolvableSet(Collection<GoogDependencyNode> dependencies) {
                return defaultHelper.getResolvableSet(dependencies);
            }

            @Override
            public List<GoogDependencyNode> getBaseList(Collection<GoogDependencyNode> dependencies) {
                return Stream.concat(
                        defaultHelper.getBaseList(dependencies).stream(),
                        baseList.stream()
                ).distinct().collect(toList());
            }
        };
    }
}
