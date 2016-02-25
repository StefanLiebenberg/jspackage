package org.slieb.tools.jspackage.mojos;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.slieb.jspackage.compile.nodes.ModuleGroupCompileNode;
import org.slieb.jspackage.compile.nodes.SingleCompileNode;
import org.slieb.jspackage.compile.nodes.SingleModuleCompileNode;
import org.slieb.jspackage.compile.result.CompileResult;
import org.slieb.jspackage.compile.result.ModuleGroupCompilationResult;
import org.slieb.jspackage.compile.tasks.ModuleCompileTask;
import org.slieb.jspackage.compile.tasks.SingleCompileTask;
import org.slieb.kute.Kute;
import org.slieb.kute.KuteIO;
import org.slieb.kute.api.Resource;
import org.slieb.kute.providers.ZipStreamResourceProvider;
import org.slieb.tools.jspackage.internal.SourceSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import static java.util.stream.Collectors.toSet;
import static org.slieb.throwables.ConsumerWithThrowable.castConsumerWithThrowable;

@Mojo(name = "compile",
        defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class CompileJavascriptMojo extends AbstractPackageMojo {

    @Parameter(name = "moduleCompiles")
    public List<ModulesCompilesConfig> moduleCompiles;

    @Parameter(name = "compiles")
    public List<SingleCompileConfig> compiles;

    @Inject
    @Named("compilerOptions")
    private Provider<CompilerOptions> compilerOptionsProvider;

    @Inject
    private ModuleCompileTask moduleCompileTask;

    @Inject
    private SingleCompileTask singleCompileTask;

    protected ZipInputStream externsZipInputStream() {
        InputStream input = Compiler.class.getResourceAsStream("/externs.zip");
        if (input == null) {
            input = Compiler.class.getResourceAsStream("externs.zip");
        }
        return new ZipInputStream(input);
    }

    protected Resource.Provider getDefaultExterns() {
        return new ZipStreamResourceProvider(this::externsZipInputStream);
    }

    protected Resource.Provider getExternsProvider(SourceSet... sourceSets) {
        return Kute.group(getDefaultExterns(), getSourceProvider(false, sourceSets));
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getInjector().injectMembers(this);
        performModuleCompiles();
        performSingleCompiles();
    }

    private void performSingleCompiles() {
        if (compiles != null && !compiles.isEmpty()) {
            getLog().info(String.format("found %s compiles to perform", compiles.size()));
            compiles.forEach(this::performSingleCompile);
        }
    }

    private void performSingleCompile(SingleCompileConfig singleCompileConfig) {
        getLog().info("performing single compile");
        getLog().info(singleCompileConfig.toString());
        final CompileResult compileResult = singleCompileTask.perform(getSingleCompileNode(singleCompileConfig));
        switch (compileResult.getType()) {
            case SUCCESS:
                CompileResult.Success success = (CompileResult.Success) compileResult;
                singleCompileConfig.getOutput()
                                   .ifPresent(file -> {
                                       try {
                                           writeResource(success.getCompiledResource(), file);
                                       } catch (IOException e) {
                                           throw new RuntimeException(e);
                                       }
                                   });
                singleCompileConfig.getSourceMapOutput()
                                   .ifPresent(file -> {
                                       try {
                                           writeResource(success.getSourceMapResource(), file);
                                       } catch (IOException e) {
                                           throw new RuntimeException(e);
                                       }
                                   });
                break;
            case FAILURE:
                throw new IllegalStateException(String.format("There was a failure compiling %s", singleCompileConfig));
        }
    }

    private SingleCompileNode getSingleCompileNode(SingleCompileConfig singleCompileConfig) {
        Resource.Provider externs = getDefaultExterns();
        Resource.Provider sources = getSourceProvider(false, flattenToArray(getMainSourceSet(), singleCompileConfig.getMainSourceSet()));
        final CompilerOptions options = compilerOptionsProvider.get();
        final Set<String> requiresSet = singleCompileConfig.getRequires().stream().collect(Collectors.toSet());
        return new SingleCompileNode(sources, externs, options, requiresSet,
                                     singleCompileConfig.getJsDefinesFile().map(Kute::fileResource).orElse(null),
                                     singleCompileConfig.getCssRenameMap().map(Kute::fileResource).orElse(null));
    }

    @SafeVarargs
    private final SourceSet[] flattenToArray(final Optional<SourceSet>... optionals) {
        return Arrays.stream(optionals).filter(Optional::isPresent).map(Optional::get).toArray(SourceSet[]::new);
    }

    private void performModuleCompiles() {
        if (moduleCompiles != null && !moduleCompiles.isEmpty()) {
            getLog().info(String.format("found %s module compiles to perform", moduleCompiles.size()));
            moduleCompiles.forEach(this::performModuleCompile);
        }
    }

    private void performModuleCompile(ModulesCompilesConfig modulesCompilesConfig) {
        getLog().info("performing module compile");
        getLog().info(modulesCompilesConfig.toString());
        ModuleGroupCompilationResult result = moduleCompileTask.perform(getModuleCompileNode(modulesCompilesConfig));
        switch (result.getType()) {
            case SUCCESS:
                ModuleGroupCompilationResult.Success success = (ModuleGroupCompilationResult.Success) result;
                getLog().error("Success:");
                modulesCompilesConfig.getDirectory()
                                     .filter(dir -> dir.exists() || dir.mkdirs())
                                     .ifPresent(dir -> writeModuleSuccessToFile(success, dir));
                break;
            case FAILURE:
                ModuleGroupCompilationResult.Failure failure = (ModuleGroupCompilationResult.Failure) result;
                getLog().error("Failure:");
                getLog().error(failure.toString());
                throw new IllegalStateException("There was a failure compiling modules");
            default:
                throw new IllegalStateException("?!");
        }
    }

    private void writeModuleSuccessToFile(ModuleGroupCompilationResult.Success success,
                                          File dir) {
        success.getModuleUnits().stream()
               .map(ModuleGroupCompilationResult.ModuleUnitCompilationResult::getResource)
               .forEach(castConsumerWithThrowable(resource -> writeResource(resource, new File(dir, resource.getPath()))));
    }

    // todo, common field is hard-coded
    // todo, custom sourceSets for every compileNode?
    private ModuleGroupCompileNode getModuleCompileNode(ModulesCompilesConfig modulesCompilesConfig) {
        CompilerOptions options = compilerOptionsProvider.get();
        Resource.Provider externsProvider = getExternsProvider(flattenToArray(getExternsSourceSet(), modulesCompilesConfig.getExternsSourceSet()));
        Resource.Provider sources = getSourceProvider(false, flattenToArray(getMainSourceSet(), modulesCompilesConfig.getMainSourceSet()));
        return new ModuleGroupCompileNode(sources, externsProvider, options, getSingleModuleCompileNodes(modulesCompilesConfig),
                                          modulesCompilesConfig.getCommonModule().orElse("common"),
                                          modulesCompilesConfig.getJsDefines().map(Kute::fileResource).orElse(null),
                                          modulesCompilesConfig.getCssRenameMap().map(Kute::fileResource).orElse(null));
    }

    private Set<SingleModuleCompileNode> getSingleModuleCompileNodes(ModulesCompilesConfig modulesCompilesConfig) {
        return modulesCompilesConfig.getModules().stream()
                                    .map(this::convertCompileConfigIntoCompileNode).collect(toSet());
    }

    private SingleModuleCompileNode convertCompileConfigIntoCompileNode(ModuleConfig moduleConfig) {
        return new SingleModuleCompileNode(moduleConfig.name, moduleConfig.dependencies, moduleConfig.requires);
    }

    private void writeResource(final Resource.Readable resource,
                               final File file) throws IOException {
        KuteIO.copyResourceWithStreams(resource, Kute.fileResource(file));
    }
}

