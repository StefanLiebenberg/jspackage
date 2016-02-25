package org.slieb.jspackage.compile.tasks;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import org.slieb.dependencies.ModuleNode;
import org.slieb.dependencies.ModuleResolver;
import org.slieb.jspackage.compile.nodes.ModuleGroupCompileNode;
import org.slieb.jspackage.compile.resources.JSModuleResource;
import org.slieb.jspackage.compile.resources.SourceMapResource;
import org.slieb.jspackage.compile.result.ModuleGroupCompilationResult;
import org.slieb.jspackage.compile.result.ModuleGroupCompilationResult.ModuleUnitCompilationResult;
import org.slieb.jspackage.dependencies.GoogDependencyNode;
import org.slieb.jspackage.dependencies.GoogResources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class ModuleCompileTask implements Task<ModuleGroupCompileNode, ModuleGroupCompilationResult> {

    @Override
    public ModuleGroupCompilationResult perform(ModuleGroupCompileNode compileNode) {
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

    private List<JSModule> getJsModules(final ModuleGroupCompileNode compileNode) {
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
            module.setDepth(calcDepth(modules, moduleNode.getName()));
            return module;
        }).collect(Collectors.toList());
    }

    private int calcDepth(List<ModuleNode<GoogDependencyNode>> modules,
                          String moduleName) {
        return modules.stream()
                      .filter(mod -> mod.getName().equals(moduleName))
                      .flatMap(mod -> mod.getModuleDependencies().stream())
                      .mapToInt(depMod -> calcDepth(modules, depMod) + 1)
                      .max().orElse(0);
    }

    private List<ModuleNode<GoogDependencyNode>> getModuleNodes(ModuleGroupCompileNode compileNode) {
        final ModuleResolver<GoogDependencyNode> resolver = createModuleResolver(compileNode);
        compileNode.getModuleCompiles().forEach(moduleCompile -> {
            final String name = moduleCompile.getName();
            resolver.resolveModule(name);
            moduleCompile.getModuleDependencies().forEach(dep -> resolver.resolveModuleDependency(name, dep));
            moduleCompile.getNamespaces().forEach(ns -> resolver.resolveModuleWithNamespace(name, ns));
        });
        return resolver.resolve();
    }

    private ModuleResolver<GoogDependencyNode> createModuleResolver(ModuleGroupCompileNode compileNode) {
        final List<GoogDependencyNode> extraBaseList =
                Stream.of(compileNode.getJsDefines(), compileNode.getCssRenameMap())
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .map(GoogResources::parse)
                      .collect(Collectors.toList());

        return GoogResources.getModuleResolver(compileNode.getSourcesProvider(), compileNode.getCommonModuleName(),
                                               GoogResources.getHelper(extraBaseList));
    }

    private Map<String, JSModule> createModuleMap(List<ModuleNode<GoogDependencyNode>> modules) {
        return modules.stream()
                      .map(ModuleNode::getName)
                      .map(JSModule::new)
                      .collect(toMap(JSModule::getName, Function.identity()));
    }

    private ModuleGroupCompilationResult buildFailure() {
        return new ModuleGroupCompilationResult.Failure();
    }

    private ModuleGroupCompilationResult buildSuccess(final Compiler compiler,
                                                      final List<JSModule> jsmodules) {
        final Set<ModuleUnitCompilationResult> moduleUnits = buildModuleUnits(compiler, jsmodules);
        final SourceMapResource sourceMapResource = new SourceMapResource(":sourceMap", compiler.getSourceMap());
        return new ModuleGroupCompilationResult.Success(moduleUnits, sourceMapResource);
    }

    private Set<ModuleUnitCompilationResult> buildModuleUnits(
            final Compiler compiler,
            final List<JSModule> jsmodules) {
        return jsmodules.stream()
                        .map(jsModule -> new JSModuleResource(compiler, jsModule))
                        .map(ModuleUnitCompilationResult::new)
                        .collect(toSet());
    }
}
