package org.slieb.tools.jspackage.mojos;


import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class ModuleConfig implements Serializable {
    public String name;
    public Set<String> dependencies;
    public Set<String> namespaces;
    public boolean standalone = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleConfig)) return false;
        ModuleConfig that = (ModuleConfig) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(dependencies, that.dependencies) &&
                Objects.equals(namespaces, that.namespaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dependencies, namespaces);
    }

    @Override
    public String toString() {
        return "ModuleConfig{" +
                "name='" + name + '\'' +
                ", dependencies=" + dependencies +
                ", namespaces=" + namespaces +
                '}';
    }
}
