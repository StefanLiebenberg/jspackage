package org.slieb.closure.javascript;


import com.google.common.base.Predicates;
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

import static com.google.javascript.jscomp.NodeUtil.visitPreOrder;
import static com.google.javascript.jscomp.parsing.ParserRunner.createConfig;

public class GoogDependencyParser implements DependencyParser<SourceFile, GoogDependencyNode> {

    private final Config config;

    private final ErrorReporter errorReporter;

    public GoogDependencyParser() {
        config = createConfig(true, Config.LanguageMode.ECMASCRIPT6_STRICT, true, null);
        errorReporter = new SimpleErrorReporter();
    }
    
    @Override
    public GoogDependencyNode parse(SourceFile resource) {
        try {
            return new Visitor(resource).parse(ParserRunner.parse(resource, resource.getCode(), config, errorReporter).ast);
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("Could not parse dependencies of %s", resource), ioException);
        }
    }
}

class Builder {

    private Boolean isBaseFile = false;
    private final ImmutableSet.Builder<String> provides, requires;
    private SourceFile sourceFile;

    public Builder(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
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

    public GoogDependencyNode build() {
        return new GoogDependencyNode(sourceFile, provides.build(), requires.build(), isBaseFile);
    }
}

class Visitor implements NodeUtil.Visitor {

    private final Builder builder;

    public Visitor(SourceFile sourceFile) {
        this.builder = new Builder(sourceFile);
    }

    @Override
    public void visit(Node node) {

        if (!node.hasChildren()) return;

        switch (node.getType()) {
            case Token.CALL:
                switch (node.getFirstChild().getQualifiedName()) {
                    case "goog.require":
                        builder.addRequire(node.getChildAtIndex(1).getString());
                        break;
                    case "goog.provide":
                        builder.addProvide(node.getChildAtIndex(1).getString());
                        break;
                }
                break;
            case Token.ASSIGN:
                switch (node.getFirstChild().getQualifiedName()) {
                    case "goog.base":
                        builder.isBase();
                        break;
                }
                break;

        }
    }

    public GoogDependencyNode parse(Node rootNode) {
        visitPreOrder(rootNode, this, Predicates.<Node>alwaysTrue());
        return builder.build();
    }
}


