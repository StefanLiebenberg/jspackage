package org.slieb.closure.gss;


import com.google.common.collect.ImmutableMap;
import com.google.common.css.SubstitutionMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class ProductionSubstitutionMap implements SubstitutionMap {

    private final ImmutableMap<String, String> map;

    public ProductionSubstitutionMap(Map<String, String> map) {
        this(ImmutableMap.copyOf(map));
    }

    public ProductionSubstitutionMap(ImmutableMap<String, String> map) {
        this.map = map;
    }

    @Override
    public String get(String key) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = Arrays.asList(key.split("-")).iterator();
        while (iterator.hasNext()) {
            String part = iterator.next();
            builder.append(map.getOrDefault(part, part));
            if (iterator.hasNext()) {
                builder.append("-");
            }
        }
        return builder.toString();
    }
}
