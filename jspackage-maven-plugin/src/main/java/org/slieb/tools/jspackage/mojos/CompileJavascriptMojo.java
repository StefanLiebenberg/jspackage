package org.slieb.tools.jspackage.mojos;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.slieb.jspackage.compile.*;
import org.slieb.jspackage.service.providers.AbstractStreamsProvider;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Mojo(name = "compile",
        defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class CompileJavascriptMojo extends AbstractPackageMojo {

    private final ModuleCompileTask moduleCompileTask;
    private final SingleCompileTask singleCompileTask;

    @Parameter()
    public List<String> inputs;

    @Parameter(name = "outputFile")
    public File outputFile;

    @Parameter(name = "moduleCompiles")
    public List<ModuleCompileConfig> moduleCompiles;

    @Parameter(name = "compiles")
    public List<CompileConfig> compiles;


    @Inject
    @Named("compilerOptions")
    Provider<CompilerOptions> compilerOptionsProvider;


    public CompileJavascriptMojo() {
        singleCompileTask = new SingleCompileTask();
        moduleCompileTask = new ModuleCompileTask();
    }

    private Resource.Readable getReadable(Supplier<ZipInputStream> zipSupplier,
                                          String name,
                                          Long limit) {
        return Kute.inputStreamResource(name,
                                        () -> new BufferedInputStream(ByteStreams.limit(zipSupplier.get(), limit)));
    }

    protected ZipInputStream zipInputStream() {
        InputStream input = Compiler.class.getResourceAsStream("/externs.zip");
        if (input == null) {
            input = Compiler.class.getResourceAsStream("externs.zip");
        }
        return new ZipInputStream(input);
    }

    protected ResourceProvider<? extends Resource.Readable> getExterns() {
        final Supplier<ZipInputStream> zipInputStreamSupplier = this::zipInputStream;
        return new AbstractStreamsProvider() {
            @Override
            protected Stream<Stream<? extends Resource.Readable>> streams() {
                Stream.Builder<Resource.Readable> builder = Stream.builder();
                try (ZipInputStream zip = zipInputStreamSupplier.get()) {
                    for (ZipEntry entry = null; (entry = zip.getNextEntry()) != null; ) {
                        builder.add(getReadable(zipInputStreamSupplier, entry.getName(), entry.getSize()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Stream.of(builder.build());
            }
        };
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getInjector().injectMembers(this);

        performModuleCompiles();
        performSingleCompiles();

        if (inputs == null || inputs.isEmpty() || sources == null || sources.isEmpty()) {
            getLog().warn("no input, skipping compile");
            return;
        }

        ResourceProvider<? extends Resource.Readable> externs = getExterns();


        MojoModule module = new MojoModule();
        module.setInputNamespaces(inputs);
        MojoConfiguration configuration = new MojoConfiguration();
        configuration.setCompilerOptions(compilerOptionsProvider.get());
        configuration.setExterns(externs);
        configuration.setModules(Lists.newArrayList(module));
        configuration.setSource(getSourceProvider(false));
        CompilerProvider compilerProvider = new CompilerProvider(configuration);
        Resource.Readable compiled = compilerProvider.getCompiledResource();

        if (outputFile.getParentFile().exists() || outputFile.getParentFile().mkdirs()) {
            Resource.Writable outputResource = Kute.fileResource(outputFile);
            try {
                Kute.copyResource(compiled, outputResource);
            } catch (IOException e) {
                throw new MojoExecutionException("failed", e);
            }
        }

    }

    private void performSingleCompiles() {
        if (compiles != null && !compiles.isEmpty()) {
            getLog().info(String.format("found %s compiles to perform", compiles.size()));
            compiles.forEach(this::performSingleCompile);
        }
    }

    private void performSingleCompile(CompileConfig compileConfig) {
        getLog().info("performing single compile");
        getLog().info(compileConfig.toString());
        final CompileResult compileResult = singleCompileTask.performCompile(getCompileNode(compileConfig));
        switch (compileResult.getType()) {
            case FAILURE:
                throw new IllegalStateException(String.format("There was a failure compiling %s", compileConfig));
        }
    }

    private SingleCompileNode getCompileNode(CompileConfig compileConfig) {
        ResourceProvider<Resource.Readable> externs = Kute.asReadableProvider(getExterns());
        ResourceProvider<Resource.Readable> sources = Kute.asReadableProvider(getSourceProvider(false));
        CompilerOptions options = compilerOptionsProvider.get();
        return new SingleCompileNode(sources, externs, options, compileConfig.inputs);
    }

    private void performModuleCompiles() {
        if (moduleCompiles != null && !moduleCompiles.isEmpty()) {
            getLog().info(String.format("found %s module compiles to perform", moduleCompiles.size()));
            moduleCompiles.forEach(this::performModuleCompile);
        }
    }

    private void performModuleCompile(ModuleCompileConfig moduleCompileConfig) {
        getLog().info("performing module compile");
        getLog().info(moduleCompileConfig.toString());
        ModuleCompileResult result = moduleCompileTask.performCompile(getCompileNode(moduleCompileConfig));
        switch (result.getType()) {
            case SUCCESS:
                ModuleCompileResult.Success success = (ModuleCompileResult.Success) result;
                getLog().error("Success:");
                Optional.ofNullable(moduleCompileConfig.directory)
                        .filter(dir -> dir.exists() || dir.mkdirs())
                        .ifPresent(dir -> {
                            success.getOutputResources().stream().forEach(resource -> {
                                try (Writer writer = new FileWriter(new File(dir, resource.getPath()));
                                     Reader reader = resource.getReader()) {
                                    IOUtils.copy(reader, writer);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        });
                break;
            case FAILURE:
                ModuleCompileResult.Failure failure = (ModuleCompileResult.Failure) result;
                getLog().error("Failure:");
                getLog().error(failure.toString());
                throw new IllegalStateException("There was a failure compiling modules");
            default:
                throw new IllegalStateException("?!");
        }
    }

    // todo, common field is hard-coded
    // todo, custom sourceSets for every compileNode?
    private ModuleCompileNode getCompileNode(ModuleCompileConfig moduleCompileConfig) {
        CompilerOptions options = compilerOptionsProvider.get();
        ResourceProvider<Resource.Readable> externs = Kute.asReadableProvider(getExterns());
        ResourceProvider<Resource.Readable> sources = Kute.asReadableProvider(getSourceProvider(false));
        return new ModuleCompileNode(sources, externs, options, getSingleModuleCompileNodes(moduleCompileConfig), moduleCompileConfig.commonModule);
    }


    private Set<SingleModuleCompileNode> getSingleModuleCompileNodes(ModuleCompileConfig moduleCompileConfig) {
        return moduleCompileConfig.modules.stream().map(moduleConfig -> {
            return new SingleModuleCompileNode(moduleConfig.name, moduleConfig.dependencies, moduleConfig.namespaces);
        }).collect(Collectors.toSet());
    }
}


class MojoConfiguration implements Configuration {

    private ResourceProvider<? extends Resource.Readable> source, externs;
    private List<Module> modules;
    private CompilerOptions compilerOptions;

    @Override
    public ResourceProvider<? extends Resource.Readable> getSourceProvider() {
        return source;
    }

    @Override
    public ResourceProvider<? extends Resource.Readable> getExternsProvider() {
        return externs;
    }

    @Override
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }


    public void setSource(ResourceProvider<? extends Resource.Readable> source) {
        this.source = source;
    }

    public void setExterns(ResourceProvider<? extends Resource.Readable> externs) {
        this.externs = externs;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void setCompilerOptions(CompilerOptions compilerOptions) {
        this.compilerOptions = compilerOptions;
    }
}

class MojoModule implements Configuration.Module {

    private List<String> inputNamespaces;

    private Resource.Writable outputResource;

    @Override
    public List<String> getInputNamespaces() {
        return inputNamespaces;
    }

    @Override
    public Resource.Writable getOutputResource() {
        return outputResource;
    }

    public void setInputNamespaces(List<String> inputNamespaces) {
        this.inputNamespaces = inputNamespaces;
    }

    public void setOutputResource(Resource.Writable outputResource) {
        this.outputResource = outputResource;
    }
}