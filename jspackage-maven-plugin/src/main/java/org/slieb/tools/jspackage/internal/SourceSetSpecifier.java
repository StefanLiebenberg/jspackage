package org.slieb.tools.jspackage.internal;


import javax.annotation.Nonnull;
import java.util.Optional;

public interface SourceSetSpecifier {

    @Nonnull
    Optional<SourceSet> getMainSourceSet();

    @Nonnull
    Optional<SourceSet> getTestSourceSet();

    @Nonnull
    Optional<SourceSet> getExternsSourceSet();
}
