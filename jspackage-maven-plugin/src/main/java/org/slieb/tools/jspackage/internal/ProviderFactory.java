package org.slieb.tools.jspackage.internal;

import org.slieb.kute.Kute;
import org.slieb.kute.KutePredicates;
import org.slieb.kute.api.Resource;
import org.slieb.kute.providers.DirectoryProvider;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class ProviderFactory {

    private final Resource.Provider classpath;

    public ProviderFactory(Resource.Provider classpath) {
        this.classpath = classpath;
    }

    public Resource.Provider create(SourceSet... sourceSets) {
        return Kute.group(Arrays.stream(sourceSets).map(this::create).toArray(Resource.Provider[]::new));
    }

    public Resource.Provider create(SourceSet sourceSet) {
        Resource.Provider provider = createProviderFromSources(sourceSet.getSources());
        return this.createFilter(sourceSet.getIncludes(), sourceSet.getExcludes())
                   .map(predicate -> Kute.filterResources(provider, predicate)).orElse(provider);
    }

    private Optional<Resource.Predicate> createFilter(Set<String> includes,
                                                      Set<String> excludes) {
        return Stream.<Resource.Predicate>concat(
                includes.stream().map(KutePredicates::patternFilter),
                excludes.stream().map(KutePredicates::patternFilter).map(predicate -> predicate.negate()::test))
                .reduce(getPredicateBinaryOperator());
    }

    private BinaryOperator<Resource.Predicate> getPredicateBinaryOperator() {
        return (predicateA, predicateB) -> {
            return (resource) -> {
                return predicateA.test(resource) && predicateB.test(resource);
            };
        };
    }

    public Resource.Provider createProviderFromSources(Collection<String> stringCollection) {
        return Kute.group(stringCollection.stream().map(this::create).toArray(Resource.Provider[]::new));
    }

    public Resource.Provider create(String sourceString) {

        if (sourceString.startsWith("classpath:")) {
            return createClasspathProvider(sourceString.substring("classpath:".length()));
        }

        if (sourceString.startsWith("file:")) {
            return createFileProvider(sourceString.substring("file:".length()));
        }

        return createFileProvider(sourceString);
    }

    private Resource.Provider createFileProvider(String substring) {
        return new DirectoryProvider(new File(substring));
    }

    private Resource.Provider createClasspathProvider(String path) {
        return Kute.filterResources(classpath, (resource) -> resource.getPath().startsWith(path));
    }
}
