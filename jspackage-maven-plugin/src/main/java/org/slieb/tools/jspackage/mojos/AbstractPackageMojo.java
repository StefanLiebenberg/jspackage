package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slieb.tools.jspackage.internal.DefaultsModule;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;
import slieb.kute.resources.providers.URLClassLoaderResourceProvider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.slieb.closure.javascript.GoogResources.getResourceProviderForSourceDirectories;


public abstract class AbstractPackageMojo extends AbstractMojo {

    @Component
    protected MavenProject project;

    @Parameter(name = "sources")
    public List<File> sources;

    @Parameter(name = "useClasspath", defaultValue = "false")
    public Boolean useClasspath;

    @Parameter(name = "guiceModule")
    public String guiceModule;

    protected ResourceProvider<? extends Resource.Readable> getSourceResources() {

        ImmutableList.Builder<ResourceProvider<? extends Resource.Readable>> builder = ImmutableList.builder();
        getLog().debug("setting resource provider for project sources");

        if (useClasspath) {
            builder.add(new URLClassLoaderResourceProvider((URLClassLoader) getClass().getClassLoader()));
            getLog().debug("adding classpath dependencies to resource provider");
        }

        if (sources != null && !sources.isEmpty()) {
            getLog().debug("adding " + sources.size() + " source directories to resource provider.");
            builder.add(getResourceProviderForSourceDirectories(sources));
        } else {
            getLog().warn("JSPackage source directories have not been specified or is empty.");
        }

        return new GroupResourceProvider<>(builder.build());
    }

    protected ResourceProvider<? extends Resource.Readable> getSourceResource(List<File> additionalDirectories) {
        ResourceProvider<? extends Resource.Readable> sourceResources = getSourceResources();
        ResourceProvider<? extends Resource.Readable> testResources = getResourceProviderForSourceDirectories(additionalDirectories);
        return new GroupResourceProvider<>(ImmutableList.of(sourceResources, testResources));
    }


    protected Module getDefaultsModule() {
        return new DefaultsModule();
    }

    protected Injector getInjector() throws MojoFailureException {
        ImmutableList.Builder<Module> modules = new ImmutableList.Builder<>();
        modules.add(getDefaultsModule());
        if (guiceModule != null) {
            try {
                Class<?> guiceClass = getCustomClassLoader(true, true).loadClass(guiceModule);
                System.out.println(guiceClass);
                Object object = guiceClass.newInstance();
                if (object instanceof Module) {
                    modules.add((Module) object);
                } else {
                    throw new MojoFailureException("guiceModule is does not extend Module");
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new MojoFailureException("cannot create " + guiceModule, e);
            }
        }

        return Guice.createInjector(modules.build());
    }

    private URLClassLoader loader;

    protected URLClassLoader getCustomClassLoader(Boolean runtime, Boolean test) {
        if (loader == null) {
            ImmutableList.Builder<String> list = new ImmutableList.Builder<>();
            try {
                list.addAll(project.getCompileClasspathElements());
                if (runtime) {
                    list.addAll(project.getRuntimeClasspathElements());
                }
                if (test) {
                    list.addAll(project.getTestClasspathElements());
                }
            } catch (DependencyResolutionRequiredException e) {
                throw new RuntimeException(e);
            }
            URL[] urls = list.build().stream().map(this::safeToUrl).distinct().toArray(URL[]::new);
            System.out.println(urls.length);
            for (URL url : urls) {
                System.out.println(url);
            }
            loader = new URLClassLoader(urls, getClass().getClassLoader());
        }
        return loader;
    }

    private URL safeToUrl(String path) {
        try {
            return new File(path).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
