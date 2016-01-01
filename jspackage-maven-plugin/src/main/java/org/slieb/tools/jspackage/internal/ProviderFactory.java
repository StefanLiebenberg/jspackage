package org.slieb.tools.jspackage.internal;


import slieb.kute.Kute;
import slieb.kute.KuteLambdas;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourcePredicate;
import slieb.kute.providers.FileResourceProvider;

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


    private Optional<ResourcePredicate<Resource>> createFilter(Set<String> includes, Set<String> excludes) {
        return Stream.<ResourcePredicate<Resource>>concat(
                includes.stream().map(KuteLambdas::patternFilter),
                excludes.stream().map(KuteLambdas::patternFilter).map(predicate -> predicate.negate()::test))
                .reduce(getPredicateBinaryOperator());
    }


    private <T extends Resource> BinaryOperator<ResourcePredicate<T>> getPredicateBinaryOperator() {
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
        return new FileResourceProvider(new File(substring));
    }

    private Resource.Provider createClasspathProvider(String path) {
        return Kute.filterResources(classpath, (resource) -> resource.getPath().startsWith(path));
    }


}
