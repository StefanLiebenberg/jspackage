package org.slieb.jspackage.compile;


import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Immutable
public class SingleModuleCompileNode {

    private final String name;
    private final Set<String> moduleDependencies;
    private final Set<String> namespaces;

    public SingleModuleCompileNode(final String name,
                                   final Set<String> moduleDependencies,
                                   final Set<String> namespaces) {
        this.name = name;
        this.moduleDependencies = safeSet(moduleDependencies);
        this.namespaces = safeSet(namespaces);
    }


    public String getName() {
        return name;
    }

    public Set<String> getModuleDependencies() {
        return moduleDependencies;
    }

    public Set<String> getNamespaces() {
        return namespaces;
    }

    private static Set<String> safeSet(Set<String> unsafeSet) {
        return Collections.unmodifiableSet(unsafeSet != null ? new HashSet<>(unsafeSet) : new HashSet<>());
    }
}
