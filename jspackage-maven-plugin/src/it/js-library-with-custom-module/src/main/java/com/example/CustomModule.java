package com.example;

import com.google.inject.Inject;
import com.google.inject.multibindings.Multibinder;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.rhino.Node;
import org.slieb.tools.jspackage.internal.OptionsHandler;

import static com.google.javascript.jscomp.CustomPassExecutionTime.BEFORE_CHECKS;

public class CustomModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), OptionsHandler.class)
                .addBinding()
                .to(CustomOptionsHandler.class);
    }

}

class CustomOptionsHandler implements OptionsHandler {
    private final Provider<CustomPass> customPassProvider;

    @Inject
    public CustomOptionsHandler(Provider<CustomPass> customPassProvider) {
        this.customPassProvider = customPassProvider;
    }

    @Override
    public void handle(CompilerOptions options) {
        options.addCustomPass(BEFORE_CHECKS, customPassProvider.get());
    }
}

class CustomPass implements CompilerPass {

    private final Provider<CustomVisitor> visitorProvider;

    @Inject
    public MyPass(Provider<CustomVisitor> visitorProvider) {
        this.visitorProvider = visitorProvider;
    }

    @Override
    public void process(Node externs, Node root) {
        CustomVisitor visitor = visitorProvider.get();
        NodeUtil.visitPreOrder(root, visitor, input -> true);
    }
}

class CustomVisitor implements NodeUtil.Visitor {

    @Override
    public void visit(Node node) {
        System.out.println(node.isString());
    }
}
