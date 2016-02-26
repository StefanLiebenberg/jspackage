package org.slieb.closure.javascript.internal;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.ErrorReporterImpl;
import com.google.template.soy.SoyFileSetParser;
import com.google.template.soy.base.internal.SoyFileSupplier;
import com.google.template.soy.error.ErrorReporter;
import com.google.template.soy.shared.SoyAstCache;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.types.SoyTypeRegistry;

public class SoyNodeParser {

    private static final ErrorReporter errorReporter = new ErrorReporterImpl();

    private final ImmutableMap<String, SoyFileSupplier> soyFileSuppliers;

    private final SoyTypeRegistry registry;

    private final SoyAstCache cache;

    public SoyNodeParser(ImmutableMap<String, SoyFileSupplier> soyFileSuppliers) {
        this.soyFileSuppliers = soyFileSuppliers;
        this.registry = new SoyTypeRegistry();
        this.cache = new SoyAstCache();
    }

    public SoyFileSetNode parse() {
        return new SoyFileSetParser(registry, cache, soyFileSuppliers, null, errorReporter)
                .parse().fileSet();
    }
}

