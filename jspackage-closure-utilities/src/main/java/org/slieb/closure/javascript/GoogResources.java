package org.slieb.closure.javascript;

import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;
import slieb.kute.resources.implementations.FileResource;
import slieb.kute.resources.providers.FileResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.io.File;
import java.util.Collection;

import static java.util.stream.Collectors.toSet;
import static slieb.kute.resources.ResourceFilters.extensionFilter;

public class GoogResources {

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(Collection<File> directories) {
        return new GroupResourceProvider<>(directories.stream().distinct().map(FileResourceProvider::new).collect(toSet()));
    }

    public static ResourceProvider<FileResource> getResourceProviderForSourceDirectories(Collection<File> directories, String... exentions) {
        return Resources.filterResources(getResourceProviderForSourceDirectories(directories), extensionFilter(exentions));
    }

    public static ResourceProvider<FileResource> getUnitTests(ResourceProvider<FileResource> provider) {
        return Resources.filterResources(provider, extensionFilter(".unit-test.js"));
    }


}
