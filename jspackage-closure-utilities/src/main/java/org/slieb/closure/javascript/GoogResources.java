package org.slieb.closure.javascript;

import com.google.javascript.jscomp.SourceFile;
import org.slieb.dependencies.DependencyCalculator;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;
import slieb.kute.resources.implementations.FileResource;
import slieb.kute.resources.providers.FileResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toSet;
import static slieb.kute.resources.ResourceFilters.extensionFilter;

public class GoogResources {
    
    public static <R extends Resource.Readable> DependencyCalculator<R, GoogDependencyNode<R>> getCalculator(ResourceProvider<? extends R> resourceProvider) {
        GoogDependencyParser<R> parser = getDependencyParser();
        GoogDependencyHelper<R> helper = new GoogDependencyHelper<>();
        List<R> list = Resources.resourceProviderToList(resourceProvider);
        return new DependencyCalculator<>(list, parser, helper);
    }

    public static <R extends Resource.Readable> GoogDependencyParser<R> getDependencyParser() {
        return new GoogDependencyParser<>(GoogResources::getSourceFileFromResource);
    }

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(Collection<File> directories) {
        return new GroupResourceProvider<>(directories.stream().distinct().map(FileResourceProvider::new).collect(toSet()));
    }

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(Collection<File> directories, String... exentions) {
        return Resources.filterResources(getResourceProviderForSourceDirectories(directories), extensionFilter(exentions));
    }

    public static SourceFile getSourceFileFromResource(Resource.Readable readable) {
        try (Reader reader = readable.getReader()) {
            return SourceFile.fromReader(readable.getPath(), reader);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }


}
