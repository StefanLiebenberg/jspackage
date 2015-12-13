package org.slieb.jspackage.compile;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import org.slieb.closure.dependencies.GoogDependencyNode;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.dependencies.ModuleNode;
import org.slieb.dependencies.ModuleResolver;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.resources.providers.CollectionResourceProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class ModuleCompileTask {

    public ModuleCompileResult performCompile(ModuleCompileNode compileNode) {

        final List<JSModule> jsmodules = getJsModules(compileNode);
        final CompilerOptions options = compileNode.getCompilerOptions();
        final List<SourceFile> externs = compileNode.getExternsProvider()
                .stream().map(GoogResources::getSourceFileFromResource)
                .collect(Collectors.toList());

        final Compiler compiler = new Compiler();
        final Result result = compiler.compileModules(externs, jsmodules, options);
        if (result.success) {
            return buildSuccess(compiler, jsmodules);
        } else {
            return buildFailure();
        }

    }

    private List<JSModule> getJsModules(ModuleCompileNode compileNode) {

        final List<ModuleNode<GoogDependencyNode>> modules = getModuleNodes(compileNode);
        final Map<String, JSModule> jsModuleMap = createModuleMap(modules);
        return modules.stream().map(moduleNode -> {
            JSModule module = jsModuleMap.get(moduleNode.getName());
            moduleNode.getModuleDependencies()
                    .stream()
                    .map(jsModuleMap::get)
                    .forEach(module::addDependency);
            moduleNode.getNodes()
                    .stream()
                    .map(GoogDependencyNode::getResource)
                    .map(GoogResources::getSourceFileFromResource)
                    .forEach(module::add);
            return module;
        }).collect(Collectors.toList());
    }

    private List<ModuleNode<GoogDependencyNode>> getModuleNodes(ModuleCompileNode compileNode) {

        ModuleResolver<GoogDependencyNode> resolver =
                GoogResources.getModuleResolver(compileNode.getSourcesProvider(), compileNode.getCommonModuleName());

        compileNode.getModuleCompiles().forEach(moduleCompile -> {
            final String name = moduleCompile.getName();
            resolver.resolveModule(name);
            moduleCompile.getModuleDependencies().forEach(dep -> resolver.resolveModuleDependency(name, dep));
            moduleCompile.getNamespaces().forEach(ns -> resolver.resolveModuleWithNamespace(name, ns));
        });

        return resolver.resolve();
    }

    private Map<String, JSModule> createModuleMap(List<ModuleNode<GoogDependencyNode>> modules) {
        return modules.stream()
                .map(ModuleNode::getName)
                .map(JSModule::new)
                .collect(toMap(JSModule::getName, Function.identity()));
    }

    private ModuleCompileResult buildFailure() {
        return new ModuleCompileResult.Failure();
    }

    private ModuleCompileResult buildSuccess(final Compiler compiler,
                                             final List<JSModule> jsmodules) {
        return new ModuleCompileResult.Success(getOutputResourceProvider(compiler, jsmodules));
    }

    private CollectionResourceProvider<Resource.Readable> getOutputResourceProvider(Compiler compiler,
                                                                                    List<JSModule> jsmodules) {
        return Kute.providerOf(
                jsmodules.stream()
                        .map(jsmodule -> Kute.stringResource(jsmodule.getName(), compiler.toSource(jsmodule)))
                        .toArray(Resource.Readable[]::new));
    }


}
