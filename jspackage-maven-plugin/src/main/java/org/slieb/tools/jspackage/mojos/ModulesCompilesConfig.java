package org.slieb.tools.jspackage.mojos;


import org.slieb.tools.jspackage.internal.SourceSet;
import org.slieb.tools.jspackage.internal.SourceSetSpecifier;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ModulesCompilesConfig implements Serializable, SourceSetSpecifier {

    public String commonModule;

    public File directory;

    public SourceSet main, test, externs;

    public List<ModuleConfig> modules;

    public File cssRenameMap;

    public File jsDefinesFile;

    @Nonnull
    @Override
    public Optional<SourceSet> getExternsSourceSet() {
        return Optional.ofNullable(externs);
    }

    @Nonnull
    @Override
    public Optional<SourceSet> getMainSourceSet() {
        return Optional.ofNullable(main);
    }

    @Nonnull
    @Override
    public Optional<SourceSet> getTestSourceSet() {
        return Optional.ofNullable(test);
    }


    @Nonnull
    public Optional<String> getCommonModule() {
        return Optional.ofNullable(commonModule);
    }

    @Nonnull
    public Optional<File> getDirectory() {
        return Optional.ofNullable(directory);
    }

    @Nonnull
    public Optional<File> getCssRenameMap() {
        return Optional.ofNullable(cssRenameMap);
    }

    @Nonnull
    public Optional<File> getJsDefines() {
        return Optional.ofNullable(jsDefinesFile);
    }

    @Nonnull
    public List<ModuleConfig> getModules() {
        return Collections.unmodifiableList(Optional.ofNullable(modules).orElseGet(Collections::emptyList));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModulesCompilesConfig)) return false;
        ModulesCompilesConfig that = (ModulesCompilesConfig) o;
        return Objects.equals(commonModule, that.commonModule) &&
                Objects.equals(directory, that.directory) &&
                Objects.equals(main, that.main) &&
                Objects.equals(test, that.test) &&
                Objects.equals(externs, that.externs) &&
                Objects.equals(modules, that.modules) &&
                Objects.equals(cssRenameMap, that.cssRenameMap) &&
                Objects.equals(jsDefinesFile, that.jsDefinesFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commonModule, directory, main, test, externs, modules, cssRenameMap, jsDefinesFile);
    }

    @Override
    public String toString() {
        return "ModulesCompilesConfig{" +
                "commonModule='" + commonModule + '\'' +
                ", directory=" + directory +
                ", main=" + main +
                ", test=" + test +
                ", externs=" + externs +
                ", modules=" + modules +
                ", cssRenameMap=" + cssRenameMap +
                ", jsDefinesFile=" + jsDefinesFile +
                '}';
    }
}
