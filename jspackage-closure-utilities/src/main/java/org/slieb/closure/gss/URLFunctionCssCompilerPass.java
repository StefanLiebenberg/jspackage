package org.slieb.closure.gss;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.css.SourceCodeLocation;
import com.google.common.css.compiler.ast.*;

import java.net.URI;
import java.util.Optional;

class URLFunctionCssCompilerPass extends DefaultTreeVisitor implements CssCompilerPass {

    private final MutatingVisitController visitController;

    private final GssUrlConfiguration urlConfiguration;

    public URLFunctionCssCompilerPass(MutatingVisitController visitController,
                                      GssUrlConfiguration urlConfiguration) {
        this.visitController = visitController;
        this.urlConfiguration = urlConfiguration;
    }

    public URLFunctionCssCompilerPass(CssTree cssTree, GssUrlConfiguration urlConfiguration) {
        this.visitController = cssTree.getMutatingVisitController();
        this.urlConfiguration = urlConfiguration;
    }


    @Override
    public void runPass() {
        this.visitController.startVisit(this);
    }


    public void replaceUri(CssFunctionNode value, Optional<URI> optionalURI) {
        SourceCodeLocation sourceCodeLocation = value.getSourceCodeLocation();
        CssValueNode arg = value.getArguments().getChildAt(0);
        String strValue = arg.getValue();
        String newPath = optionalURI.map(p -> p.resolve(strValue).normalize().toString()).orElse(strValue);
        CssValueNode newValueNode = new CssLiteralNode(newPath, sourceCodeLocation);
        CssFunctionNode.Function function = CssFunctionNode.Function.byName("url");
        CssFunctionArgumentsNode newArgumentsNode = new CssFunctionArgumentsNode(Lists.newArrayList(newValueNode));
        CssFunctionNode cssFunctionNode = new CssFunctionNode(function, sourceCodeLocation);
        cssFunctionNode.setArguments(newArgumentsNode);
        visitController.replaceCurrentBlockChildWith(Lists.newArrayList(cssFunctionNode), true);
    }

    @Override
    public boolean enterFunctionNode(CssFunctionNode value) {
        switch (value.getFunctionName()) {
            case "resource-url:":
                Preconditions.checkArgument(value.getArguments().numChildren() == 1, "resource-url should only have one child");
                replaceUri(value, urlConfiguration.getResourcesUri());
                break;
            case "image-url":
                Preconditions.checkArgument(value.getArguments().numChildren() == 1, "image-url should only have one child");
                replaceUri(value, urlConfiguration.getImagesUri());
                break;
        }
        return true;
    }


}
