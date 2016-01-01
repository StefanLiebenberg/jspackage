package org.slieb.tools.jspackage.mojos;


import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class Filter {
    public Set<String> includes;
    
    public Set<String> excludes;

    public Set<String> getIncludes() {
        return Optional.ofNullable(includes).orElse(Collections.emptySet());
    }

    public Set<String> getExcludes() {
        return Optional.ofNullable(excludes).orElse(Collections.emptySet());
    }
}
