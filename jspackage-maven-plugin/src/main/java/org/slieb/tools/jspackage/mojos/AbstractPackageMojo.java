package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.implementations.FileResource;
import slieb.kute.resources.providers.GroupResourceProvider;
import slieb.kute.resources.providers.URLClassLoaderResourceProvider;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;

import static org.slieb.closure.javascript.GoogResources.getResourceProviderForSourceDirectories;


public abstract class AbstractPackageMojo extends AbstractMojo {

    @Component
 protected    MavenProject project;

    @Parameter(name = "sourceDirectories")
    public List<File> sourceDirectories;

    @Parameter(name = "useClasspath", defaultValue = "false")
    public Boolean useClasspath;

    protected ResourceProvider<? extends Resource.Readable> getSourceResources() {

        ImmutableList.Builder<ResourceProvider<? extends Resource.Readable>> builder = ImmutableList.builder();

        if (useClasspath) {
            builder.add(new URLClassLoaderResourceProvider((URLClassLoader) getClass().getClassLoader()));
        }

        ResourceProvider<FileResource> sourceProvider = getResourceProviderForSourceDirectories(sourceDirectories);
        builder.add(sourceProvider);


        return new GroupResourceProvider<>(builder.build());
    }
}
