package org.slieb.jspackage.compile;


import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import org.slieb.closure.dependencies.GoogDependencyCalculator;
import org.slieb.closure.dependencies.GoogDependencyNode;
import org.slieb.closure.dependencies.GoogResources;
import slieb.kute.Kute;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SingleCompileTask {


    public CompileResult performCompile(SingleCompileNode compileNode) {


        final CompilerOptions options = compileNode.getCompilerOptions();
        final List<SourceFile> externs =
                compileNode.getExternsProvider().stream()
                        .map(GoogResources::getSourceFileFromResource).collect(toList());

        final GoogDependencyCalculator calculator = GoogResources.getCalculator(compileNode.getSourcesProvider());
        final List<SourceFile> inputs = calculator.getDependenciesFor(compileNode.getInputNamespaces())
                .stream()
                .map(GoogDependencyNode::getResource)
                .map(GoogResources::getSourceFileFromResource)
                .collect(toList());


        final Compiler compiler = new Compiler();
        Result result = compiler.compile(externs, inputs, options);
        if (result.success) {
            return new CompileResult.Success(Kute.stringResource(":compileResult", compiler.toSource()));
        } else {
            return new CompileResult.Failure();
        }

    }


}
