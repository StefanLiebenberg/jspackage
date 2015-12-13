package org.slieb.closure.javascript.internal;


import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonPrinter {


    public String printObjectMap(Map json) {
        StringBuilder str = new StringBuilder();
        str.append("{");
        Iterator entryIterator = json.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            str.append("\"").append(entry.getKey()).append("\": ").append(toValue(entry.getValue()));
            if (entryIterator.hasNext()) {
                str.append(", ");
            }
        }
        str.append("}");
        return str.toString();
    }

    public String printStringMap(Map<String, String> json) {
        return printObjectMap(json.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> this.toObject(entry.getValue()))));
    }

    protected String toValue(Object object) {
        if (object != null) {
            if (object instanceof String) {
                return "\"" + object + "\"";
            }
            return object.toString();
        } else {
            return "null";
        }
    }

    protected Object toObject(String value) {
        if (value != null && !value.equals("null")) {
            switch (value) {
                case "true":
                case "TRUE":
                    return Boolean.TRUE;
                case "false":
                case "FALSE":
                    return Boolean.FALSE;
            }

            if (value.matches("\\d+")) {
                return Integer.valueOf(value);
            }

            if (value.matches("\\d+\\.\\d+")) {
                return Double.valueOf(value);
            }
            return value;
        } else {
            return null;
        }
    }


}
