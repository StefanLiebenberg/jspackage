package org.slieb.jspackage.compile.tasks;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import org.slieb.jspackage.compile.nodes.SingleCompileNode;
import org.slieb.jspackage.compile.resources.CompiledResource;
import org.slieb.jspackage.compile.resources.DebugResource;
import org.slieb.jspackage.compile.resources.SourceMapResource;
import org.slieb.jspackage.compile.result.CompileResult;
import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.jspackage.dependencies.GoogDependencyNode;
import org.slieb.jspackage.dependencies.GoogResources;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SingleCompileTask implements Task<SingleCompileNode, CompileResult> {

    /**
     * Perform a standard compile.
     *
     * @param compileNode A compile node built for this task.
     * @return A compile result.
     */
    @Override
    public CompileResult perform(SingleCompileNode compileNode) {
        final Compiler compiler = new Compiler();
        final Result result = compiler.compile(
                getExterns(compileNode),
                getInputs(compileNode),
                compileNode.getCompilerOptions());
        if (result.success) {
            final CompiledResource compiledResource = new CompiledResource(":compileResult", compiler);
            final SourceMapResource sourceMapResource = new SourceMapResource(":sourceMap", compiler);
            final DebugResource errorsResource = new DebugResource(":debug", result);
            return new CompileResult.Success(compiledResource, sourceMapResource, errorsResource);
        } else {
            return new CompileResult.Failure();
        }
    }

    /**
     * @param compileNode Compile node for this task.
     * @return A list of externs.
     */
    private List<SourceFile> getExterns(SingleCompileNode compileNode) {
        return compileNode.getExternsProvider().stream().map(GoogResources::getSourceFileFromResource).collect(toList());
    }

    /**
     * @param compileNode The compile node for this task
     * @return a list of inputs.
     */
    private List<SourceFile> getInputs(SingleCompileNode compileNode) {
        final GoogDependencyCalculator calculator = createDependencyCalculator(compileNode);
        return calculator.getDependenciesFor(compileNode.getRequiredNamespaces())
                         .stream()
                         .map(GoogDependencyNode::getResource)
                         .map(GoogResources::getSourceFileFromResource)
                         .collect(toList());
    }

    /**
     * @param compileNode The compile node for this task.
     * @return A dependency calculator
     */
    private GoogDependencyCalculator createDependencyCalculator(SingleCompileNode compileNode) {
        final List<GoogDependencyNode> extraBaseList =
                Stream.of(compileNode.getJsDefines(), compileNode.getCssRenameMap())
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .map(GoogResources::parse)
                      .collect(Collectors.toList());
        return new GoogDependencyCalculator(compileNode.getSourcesProvider(),
                                            GoogResources.getDependencyParser(),
                                            GoogResources.getHelper(extraBaseList));
    }
}
