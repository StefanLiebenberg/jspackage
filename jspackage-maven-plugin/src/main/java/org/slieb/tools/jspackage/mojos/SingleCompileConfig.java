package org.slieb.tools.jspackage.mojos;


import org.slieb.tools.jspackage.internal.SourceSet;
import org.slieb.tools.jspackage.internal.SourceSetSpecifier;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SingleCompileConfig implements SourceSetSpecifier, Serializable {

    public String name;

    public SourceSet main, test, externs;

    public Set<String> requires;

    public File cssRenameMap;

    public File jsDefinesFile;

    public File output;

    public File sourceMapOutput;

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
    public Set<String> getRequires() {
        return SourceSet.populatedSetOrEmpty(requires);
    }

    @Nonnull
    public Optional<File> getOutput() {
        return Optional.ofNullable(output);
    }

    @Nonnull
    public Optional<File> getCssRenameMap() {
        return Optional.ofNullable(cssRenameMap);
    }

    @Nonnull
    public Optional<File> getJsDefinesFile() {
        return Optional.ofNullable(jsDefinesFile);
    }

    @Nonnull
    public Optional<File> getSourceMapOutput() {
        return Optional.ofNullable(sourceMapOutput);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SingleCompileConfig)) return false;
        SingleCompileConfig that = (SingleCompileConfig) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(main, that.main) &&
                Objects.equals(test, that.test) &&
                Objects.equals(externs, that.externs) &&
                Objects.equals(requires, that.requires) &&
                Objects.equals(cssRenameMap, that.cssRenameMap) &&
                Objects.equals(jsDefinesFile, that.jsDefinesFile) &&
                Objects.equals(output, that.output) &&
                Objects.equals(sourceMapOutput, that.sourceMapOutput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, main, test, externs, requires, cssRenameMap, jsDefinesFile, output, sourceMapOutput);
    }

    @Override
    public String toString() {
        return "SingleCompileConfig{" +
                "name='" + name + '\'' +
                ", main=" + main +
                ", test=" + test +
                ", externs=" + externs +
                ", requires=" + requires +
                ", cssRenameMap=" + cssRenameMap +
                ", jsDefinesFile=" + jsDefinesFile +
                ", output=" + output +
                ", sourceMapOutput=" + sourceMapOutput +
                '}';
    }
}
