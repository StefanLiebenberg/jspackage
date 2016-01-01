package org.slieb.closure.gss;


import com.google.common.collect.ImmutableMap;
import com.google.common.css.SubstitutionMap;

import java.util.concurrent.ConcurrentHashMap;

public class DevelopmentSubstitutionMap implements SubstitutionMap {

    private static final String[] ALPHABET = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z"};

    private int counter = 0;

    private final ConcurrentHashMap<String, String> map;

    public DevelopmentSubstitutionMap() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public String get(String key) {
        StringBuilder builder = new StringBuilder();
        for (String part : key.split("\\-")) {
            builder.append(getPart(part));
        }
        return builder.toString();
    }

    public ImmutableMap getRenameMap() {
        return ImmutableMap.copyOf(map);
    }

    protected String getPart(String part) {
        if (!map.contains(part)) {
            map.put(part, getNextValue());
        }
        return map.get(part);
    }

    protected String getNextValue() {
        return getStringValue(ALPHABET, counter++);
    }

    protected static String getStringValue(final String[] alphabet,
                                           final Integer position) {
        if (position < alphabet.length) {
            return alphabet[position];
        } else {
            final int remain = position % alphabet.length;
            return getStringValue(alphabet, ((position - remain) / alphabet.length) - 1) + getStringValue(alphabet,
                                                                                                          remain);
        }
    }
}
