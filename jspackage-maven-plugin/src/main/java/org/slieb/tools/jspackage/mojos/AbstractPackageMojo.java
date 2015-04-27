package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;
import slieb.kute.resources.providers.URLClassLoaderResourceProvider;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;

import static org.slieb.closure.javascript.GoogResources.getResourceProviderForSourceDirectories;


public abstract class AbstractPackageMojo extends AbstractMojo {

    @Component
    protected MavenProject project;

    @Parameter(name = "sourceDirectories")
    public List<File> sourceDirectories;

    @Parameter(name = "useClasspath", defaultValue = "false")
    public Boolean useClasspath;

    protected ResourceProvider<? extends Resource.Readable> getSourceResources() {

        ImmutableList.Builder<ResourceProvider<? extends Resource.Readable>> builder = ImmutableList.builder();
        getLog().debug("setting resource provider for project sources");

        if (useClasspath) {
            builder.add(new URLClassLoaderResourceProvider((URLClassLoader) getClass().getClassLoader()));
            getLog().debug("adding classpath dependencies to resource provider");
        }

        if (sourceDirectories != null && !sourceDirectories.isEmpty()) {
            getLog().debug("adding " + sourceDirectories.size() + " source directories to resource provider.");
            builder.add(getResourceProviderForSourceDirectories(sourceDirectories));
        } else {
            getLog().warn("JSPackage source directories have not been specified or is empty.");
        }


        return new GroupResourceProvider<>(builder.build());
    }
}
