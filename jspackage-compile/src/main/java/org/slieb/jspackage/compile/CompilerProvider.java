package org.slieb.jspackage.compile;


import com.google.common.base.Preconditions;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogResources;
import org.slieb.jspackage.compile.resources.CompiledJSModuleResource;
import org.slieb.jspackage.compile.resources.ResultResource;
import org.slieb.jspackage.compile.resources.SourceMapResource;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CompilerProvider implements ResourceProvider<Resource.Readable> {

    private final Configuration configuration;

    private Pair<List<Pair<Configuration.Module, JSModule>>, Pair<Compiler, Result>> cacheCompileResult;

    public CompilerProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<Pair<Configuration.Module, JSModule>> getModules() {
        return new ArrayList<>();
    }

    private List<SourceFile> getExterns() {
        return configuration.getExternsProvider().stream().map(GoogResources::getSourceFileFromResource).collect(toList());
    }

    private List<JSModule> getJSModules() {

        Preconditions.checkNotNull(configuration.getModules());
        Preconditions.checkState(Integer.valueOf(1).equals(configuration.getModules().size()), "no more than one module is currently supported");

        JSModule base = new JSModule("common");
        GoogDependencyCalculator calculator = GoogResources.getCalculatorCast(configuration.getSourceProvider());

        Configuration.Module module = configuration.getModules().get(0);
//        base.add();

        return null;

    }

    public Pair<List<Pair<Configuration.Module, JSModule>>, Pair<Compiler, Result>> getCompileResult() {
        if (cacheCompileResult == null) {
            Compiler compiler = new Compiler();
            List<SourceFile> externs = getExterns();
            List<JSModule> jsModules = new ArrayList<>();
            CompilerOptions options = new CompilerOptions();
            Result result = compiler.compileModules(externs, jsModules, options);

            List<Pair<Configuration.Module, JSModule>> input = new ArrayList<>();
            Pair<Compiler, Result> output = new ImmutablePair<>(compiler, result);
            cacheCompileResult = new ImmutablePair<>(input, output);
        }
        return cacheCompileResult;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        Pair<List<Pair<Configuration.Module, JSModule>>, Pair<Compiler, Result>> compileResult = getCompileResult();
        Pair<Compiler, Result> output = compileResult.getRight();
        Result result = output.getRight();
        Resource.Readable resultResource = new ResultResource("/output/result", result);
        if (result.success) {
            Compiler compiler = output.getLeft();
            List<Pair<Configuration.Module, JSModule>> input = compileResult.getLeft();
            Stream<Resource.Readable> resourceStream = input.stream().map(pair -> new CompiledJSModuleResource(pair.getLeft().toString(), compiler, pair.getRight()));
            Resource.Readable sourceMap = new SourceMapResource("/output/sourceMap.js", compiler.getSourceMap());
            resourceStream = Stream.concat(resourceStream, Stream.of(resultResource, sourceMap));
            return resourceStream;
        } else {
            return Stream.of(resultResource);
        }
    }

    @Override
    public Resource.Readable getResourceByName(String path) {
        return Resources.findResource(stream(), path);
    }
}

