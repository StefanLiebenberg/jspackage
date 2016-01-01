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
import org.slieb.tools.jspackage.internal.ProviderFactory;
import org.slieb.tools.jspackage.internal.SourceSet;
import org.slieb.tools.jspackage.internal.SourceSetSpecifier;
import slieb.kute.Kute;
import slieb.kute.api.Resource;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.slieb.closure.dependencies.GoogResources.getResourceProviderForSourceDirectories;


public abstract class AbstractPackageMojo extends AbstractMojo implements SourceSetSpecifier {

    public final static String LOG_PREFIX = "[jspackage]";

    @Component
    protected MavenProject project;

    @Parameter(name = "main")
    public JSPackageSourceSet main;

    @Parameter(name = "test")
    public JSPackageSourceSet test;

    @Parameter(name = "externs")
    private JSPackageSourceSet externs;

    @Nonnull
    @Override
    public Optional<SourceSet> getMainSourceSet() {
        return Optional.ofNullable(main);
    }

    @Nonnull
    @Override
    public Optional<SourceSet> getTestSourceSet() {
        return Optional.ofNullable(test);
    }

    @Nonnull
    @Override
    public Optional<SourceSet> getExternsSourceSet() {
        return Optional.ofNullable(externs);
    }

    @Deprecated
    @Parameter(name = "useClasspath", defaultValue = "false")
    public Boolean useClasspath;

    @Parameter(name = "guiceModule")
    public String guiceModule;

    public Resource.Provider getClaspathProvider(Boolean inludeTesting) {
        return Kute.getProvider(getCustomClassLoader(Boolean.TRUE, inludeTesting));
    }


    @Deprecated
    public Resource.Provider getSourceProvider(Boolean includeTesting) {
        Stream.Builder<JSPackageSourceSet> builder = Stream.builder();

        if (main != null && !main.getSources().isEmpty()) {
            debug("adding %s source directories to resource provider.", main.getSources().size());
            builder.add(main);
        } else {
            warn("source directories have not been specified or is empty.", LOG_PREFIX);
        }

        if (includeTesting) {
            if (test != null && !test.getSources().isEmpty()) {
                debug("adding %s source directories to resource provider.", test.getSources().size());
                builder.add(test);
            } else {
                warn(String.format("%s no test sources are specified", LOG_PREFIX));
            }
        }

        return getSourceProvider(includeTesting, builder.build().toArray(JSPackageSourceSet[]::new));
    }

    protected Resource.Provider getSourceProvider(Boolean includeTesting, SourceSet... sourceSets) {
        return new ProviderFactory(getClaspathProvider(includeTesting)).create(sourceSets);
    }


    @Deprecated
    protected Resource.Provider getSourceProvider(Boolean includeTesting, List<File> additionalDirectories) {
        ImmutableList.Builder<Resource.Provider> builder = ImmutableList.builder();
        builder.add(getSourceProvider(includeTesting));
        if (additionalDirectories != null && !additionalDirectories.isEmpty()) {
            builder.add(getResourceProviderForSourceDirectories(additionalDirectories));
        }
        if (useClasspath) {
            builder.add(getClaspathProvider(includeTesting));
        }

        return Kute.group(builder.build());
    }

    @Deprecated
    protected Resource.Provider getPackageProvider(Boolean includeTesting) {
        ImmutableList.Builder<Resource.Provider> builder = ImmutableList.builder();
        builder.add(getSourceProvider(includeTesting));

        // add the classpath loader last, as this is a fifo system and the source directories get priority.
        if (useClasspath) {
            debug("adding classpath dependencies to resource provider");
            builder.add(getClaspathProvider(includeTesting));
        }
        return Kute.group(builder.build());
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

    protected URLClassLoader getCustomClassLoader(Boolean runtime,
                                                  Boolean test) {
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

    protected void info(String info,
                        Object... vars) {
        getLog().info(LOG_PREFIX + " " + String.format(info, vars));
    }

    protected void debug(String info,
                         Object... vars) {
        getLog().debug(LOG_PREFIX + " " + String.format(info, vars));
    }

    protected void warn(String info,
                        Object... vars) {
        getLog().warn(LOG_PREFIX + " " + String.format(info, vars));
    }

    protected void error(String info,
                         Object... vars) {
        getLog().error(LOG_PREFIX + " " + String.format(info, vars));
    }
}
