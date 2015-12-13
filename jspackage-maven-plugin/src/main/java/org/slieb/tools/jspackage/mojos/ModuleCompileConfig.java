package org.slieb.tools.jspackage.mojos;


import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ModuleCompileConfig implements Serializable {
    public List<ModuleConfig> modules;
    public File directory;

    public String commonModule;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleCompileConfig)) return false;
        ModuleCompileConfig that = (ModuleCompileConfig) o;
        return Objects.equals(modules, that.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modules);
    }

    @Override
    public String toString() {
        return "ModuleCompileConfig{" +
                "modules=" + modules +
                '}';
    }
}
