package org.slieb.closure.javascript;


import com.google.common.collect.ImmutableSet;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.ErrorReporter;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.SimpleErrorReporter;
import com.google.javascript.rhino.Token;
import org.slieb.dependencies.DependencyParser;

import java.io.IOException;
import java.util.function.Function;

import static com.google.javascript.jscomp.NodeUtil.visitPreOrder;
import static com.google.javascript.jscomp.parsing.ParserRunner.createConfig;

public class GoogDependencyParser<R> implements DependencyParser<R, GoogDependencyNode<R>> {

    private final Config config;

    private final ErrorReporter errorReporter;

    private final Function<R, SourceFile> sourceFileFunction;

    public GoogDependencyParser(Function<R, SourceFile> sourceFileFunction) {
        config = createConfig(true, Config.LanguageMode.ECMASCRIPT6_STRICT, true, null);
        errorReporter = new SimpleErrorReporter();
        this.sourceFileFunction = sourceFileFunction;
    }

    @Override
    public GoogDependencyNode<R> parse(R resource) {
        try {
            SourceFile sourceFile = sourceFileFunction.apply(resource);
            ParserRunner.ParseResult result = ParserRunner.parse(sourceFile, sourceFile.getCode(), config, errorReporter);
            return new Visitor<>(resource).parse(result.ast);
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("Could not parse dependencies of %s", resource), ioException);
        }
    }
}

class Builder<R> {

    private Boolean isBaseFile = false;
    private final ImmutableSet.Builder<String> provides, requires;
    private R resource;

    public Builder(R resource) {
        this.resource = resource;
        this.provides = new ImmutableSet.Builder<>();
        this.requires = new ImmutableSet.Builder<>();
    }

    public Builder addProvide(String provide) {
        provides.add(provide);
        return this;
    }

    public Builder addRequire(String require) {
        requires.add(require);
        return this;
    }

    public Builder isBase() {
        isBaseFile = true;
        return this;
    }

    public GoogDependencyNode<R> build() {
        return new GoogDependencyNode<>(resource, provides.build(), requires.build(), isBaseFile);
    }
}

class Visitor<R> implements NodeUtil.Visitor {

    private final Builder<R> builder;

    private boolean isDone = false;

    public Visitor(R resource) {
        this.builder = new Builder<>(resource);
    }

    @Override
    public void visit(Node node) {
        if (isDone || !node.hasChildren()) return;

        switch (node.getType()) {
            case Token.CALL:
                Node callChild = node.getFirstChild();
                if (callChild.isGetProp()) {
                    if (callChild.getQualifiedName() != null) {
                        switch (callChild.getQualifiedName()) {
                            case "goog.provide":
                                builder.addProvide(node.getChildAtIndex(1).getString());
                                break;
                            case "goog.require":
                                builder.addRequire(node.getChildAtIndex(1).getString());
                                break;
                        }
                    }
                }
                break;
            case Token.ASSIGN:
                Node assignChild = node.getFirstChild();
                if (assignChild.isGetProp()) {
                    String name = assignChild.getQualifiedName();
                    if (name != null && name.equals("goog.base")) {
                        isDone = true;
                        builder.isBase();
                    }
                }
                break;
        }
    }


    private boolean shouldVisitChildren(Node node) {
        if (isDone) return false;

        switch (node.getType()) {
            case Token.SCRIPT:
            case Token.EXPR_RESULT:
            case Token.IF:
            case Token.BLOCK:
                return true;
            case Token.FUNCTION:
            case Token.CALL:
            case Token.GETPROP:
            case Token.ASSIGN:
            default:
                return false;
        }
    }

    public GoogDependencyNode<R> parse(Node rootNode) {
        visitPreOrder(rootNode, this, this::shouldVisitChildren);
        return builder.build();
    }

}


