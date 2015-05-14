package org.slieb.closure.javascript.internal;

import com.google.template.soy.base.internal.SoyFileSupplier;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.shared.SoyAstCache;
import com.google.template.soy.soyparse.SoyFileSetParser;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.types.SoyTypeRegistry;

import java.util.List;

public class SoyNodeParser {

    private static final SyntaxVersion SYNTAX_VERSION = SyntaxVersion.V2_0;

    private final List<SoyFileSupplier> soyFileSuppliers;

    private final SoyTypeRegistry registry;

    private final SoyAstCache cache;

    public SoyNodeParser(List<SoyFileSupplier> soyFileSuppliers) {
        this.soyFileSuppliers = soyFileSuppliers;
        this.registry = new SoyTypeRegistry();
        this.cache = new SoyAstCache();
    }

    public SoyFileSetNode parse() {
        return new SoyFileSetParser(registry, cache, SYNTAX_VERSION, soyFileSuppliers).parse();
    }
}

