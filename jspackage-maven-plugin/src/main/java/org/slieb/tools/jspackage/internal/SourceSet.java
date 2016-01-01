package org.slieb.tools.jspackage.internal;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;

public interface SourceSet {

    @Nonnull
    default Set<String> getSources() {
        return unmodifiableSet(Collections.emptySet());
    }

    @Nonnull
    default Set<String> getIncludes() {
        return unmodifiableSet(Collections.emptySet());
    }

    @Nonnull
    default Set<String> getExcludes() {
        return unmodifiableSet(Collections.emptySet());
    }

    @Nonnull
    static Set<String> populatedSetOrEmpty(@Nullable Set<String> sources) {
        return unmodifiableSet(ofNullable(sources).orElseGet(Collections::emptySet));
    }


}
