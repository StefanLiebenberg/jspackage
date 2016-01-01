package org.slieb.tools.jspackage.mojos;


import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ModuleConfig {

    public String name;

    public File output;

    public Set<String> requires;

    public Set<String> dependencies;


    public Set<String> getDependencies() {
        if (dependencies != null) {
            return dependencies;
        } else {
            return Collections.emptySet();
        }
    }

    public Set<String> getRequires() {
        return Optional.ofNullable(requires).orElseGet(Collections::emptySet);
    }

    public Optional<File> getOutput() {
        return Optional.ofNullable(output);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleConfig)) return false;
        ModuleConfig that = (ModuleConfig) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(output, that.output) &&
                Objects.equals(requires, that.requires) &&
                Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, output, requires, dependencies);
    }

    @Override
    public String toString() {
        return "ModuleConfig{" +
                "name='" + name + '\'' +
                ", output=" + output +
                ", requires=" + requires +
                ", dependencies=" + dependencies +
                '}';
    }
}
