package org.slieb.tools.jspackage.mojos;


import org.slieb.tools.jspackage.internal.SourceSet;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class JSPackageSourceSet implements SourceSet, Serializable {

    public Set<String> sources, includes, excludes;

    @Nonnull
    public Set<String> getSources() {
        return SourceSet.populatedSetOrEmpty(sources);
    }

    @Nonnull
    public Set<String> getIncludes() {
        return SourceSet.populatedSetOrEmpty(includes);
    }

    @Nonnull
    public Set<String> getExcludes() {
        return SourceSet.populatedSetOrEmpty(excludes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JSPackageSourceSet)) return false;
        JSPackageSourceSet that = (JSPackageSourceSet) o;
        return Objects.equals(sources, that.sources) &&
                Objects.equals(includes, that.includes) &&
                Objects.equals(excludes, that.excludes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sources, includes, excludes);
    }

    @Override
    public String toString() {
        return "JSPackageSourceSet{" +
                "sources=" + sources +
                ", includes=" + includes +
                ", excludes=" + excludes +
                '}';
    }
}
