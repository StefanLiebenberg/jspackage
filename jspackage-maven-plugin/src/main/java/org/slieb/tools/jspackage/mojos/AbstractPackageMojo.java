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
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.providers.GroupResourceProvider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.slieb.closure.dependencies.GoogResources.getResourceProviderForSourceDirectories;


public abstract class AbstractPackageMojo extends AbstractMojo {

    public final static String LOG_PREFIX = "[jspackage]";

    @Component
    protected MavenProject project;

    @Parameter(name = "sources")
    public List<File> sources;

    @Parameter(name = "testSources")
    public List<File> testSources;

    @Parameter(name = "useClasspath", defaultValue = "false")
    public Boolean useClasspath;

    @Parameter(name = "guiceModule")
    public String guiceModule;

    public ResourceProvider<? extends Resource.InputStreaming> getClaspathProvider(Boolean inludeTesting) {
        return Kute.getProvider(getCustomClassLoader(Boolean.TRUE, inludeTesting));
    }

    public ResourceProvider<? extends Resource.InputStreaming> getTestResources() {
        return getResourceProviderForSourceDirectories(testSources != null ? testSources : ImmutableList.of());
    }

    public ResourceProvider<? extends Resource.InputStreaming> getSourceProvider(Boolean includeTesting) {
        ImmutableList.Builder<ResourceProvider<? extends Resource.InputStreaming>> builder = ImmutableList.builder();

        if (includeTesting) {
            if (testSources != null && !testSources.isEmpty()) {
                debug("adding %s source directories to resource provider.", testSources.size());
                builder.add(getTestResources());
            } else {
                warn(String.format("%s no test sources are specified", LOG_PREFIX));
            }
        }

        if (sources != null && !sources.isEmpty()) {
            debug("adding %s source directories to resource provider.", sources.size());
            builder.add(getResourceProviderForSourceDirectories(sources));
        } else {
            warn("source directories have not been specified or is empty.", LOG_PREFIX);
        }
        return new GroupResourceProvider<>(builder.build());
    }


    protected ResourceProvider<? extends Resource.InputStreaming> getSourceProvider(Boolean includeTesting, List<File> additionalDirectories) {
        ImmutableList.Builder<ResourceProvider<? extends Resource.InputStreaming>> builder = ImmutableList.builder();
        builder.add(getSourceProvider(includeTesting));
        if (additionalDirectories != null && !additionalDirectories.isEmpty()) {
            builder.add(getResourceProviderForSourceDirectories(additionalDirectories));
        }
        if (useClasspath) {
            builder.add(getClaspathProvider(includeTesting));
        }
        return new GroupResourceProvider<>(builder.build());
    }

    protected ResourceProvider<? extends Resource.InputStreaming> getPackageProvider(Boolean includeTesting) {
        ImmutableList.Builder<ResourceProvider<? extends Resource.InputStreaming>> builder = ImmutableList.builder();
        builder.add(getSourceProvider(includeTesting));

        // add the classpath loader last, as this is a fifo system and the source directories get priority.
        if (useClasspath) {
            debug("adding classpath dependencies to resource provider");
            builder.add(getClaspathProvider(includeTesting));
        }
        return new GroupResourceProvider<>(builder.build());
    }


    protected Module getDefaultsModule() {
        return new DefaultsModule();
    }

    protected Injector getInjector() throws MojoFailureException {
        ImmutableList.Builder<Module> modules = new ImmutableList.Builder<>();
        modules.add(getDefaultsModule());
        if (guiceModule != null) {
            try {
                debug("loading class %s", guiceModule);
                Class<?> guiceClass = getCustomClassLoader(true, true).loadClass(guiceModule);
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
            loader = new URLClassLoader(urls, getClass().getClassLoader());
        }
        return loader;
    }

    private URL safeFileToUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL safeToUrl(String path) {
        return safeFileToUrl(new File(path));
    }

    protected void info(String info, Object... vars) {
        getLog().info(LOG_PREFIX + " " + String.format(info, vars));
    }

    protected void debug(String info, Object... vars) {
        getLog().debug(LOG_PREFIX + " " + String.format(info, vars));
    }

    protected void warn(String info, Object... vars) {
        getLog().warn(LOG_PREFIX + " " + String.format(info, vars));
    }

    protected void error(String info, Object... vars) {
        getLog().error(LOG_PREFIX + " " + String.format(info, vars));
    }
}
