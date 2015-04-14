package org.slieb.jspackage.runtimes;


import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;

public class JavascriptRuntimeUtils {

    /**
     * Type Cast Object to Boolean.
     *
     * @param runtime
     * @param command
     * @return
     */
    public static Boolean getBooleanFromJsRuntime(JavascriptRuntime runtime, String command) {
        return (Boolean) runtime.execute(command);
    }

    /**
     * @param runtime
     * @param command
     * @return
     */
    public static String getStringFromJsRuntime(JavascriptRuntime runtime, String command) {
        return (String) runtime.execute(command);
    }

    /**
     * @param runtime
     * @param command
     * @return
     */
    public static Integer getIntegerFromJsRuntime(JavascriptRuntime runtime, String command) {
        Number number = (Number) runtime.execute(command);
        if (number != null) {
            return number.intValue();
        } else {
            return null;
        }
    }

    /**
     * @param runtime
     * @param reader
     * @param path
     * @return
     * @throws IOException
     */
    public static Object evaluateReader(JavascriptRuntime runtime, Reader reader, String path) throws IOException {
        return runtime.execute(IOUtils.toString(reader), path);
    }

    /**
     * @param runtime
     * @param stream
     * @param path
     * @return
     * @throws IOException
     */
    public static Object evaluateInputStream(JavascriptRuntime runtime, InputStream stream, String path) throws IOException {
        try (Reader reader = new InputStreamReader(stream)) {
            return evaluateReader(runtime, reader, path);
        }
    }

    public static Object evaluateResource(JavascriptRuntime runtime, ClassLoader classLoader, String resource) throws IOException {
        try (InputStream inputStream = classLoader.getResourceAsStream(resource)) {
            Preconditions.checkNotNull(inputStream, "%s is not found.", resource);
            return evaluateInputStream(runtime, inputStream, resource);
        }
    }

    /**
     * @param runtime
     * @param url
     * @return
     * @throws IOException
     */
    public static Object evaluateURL(JavascriptRuntime runtime, URL url) throws IOException {
        try (InputStream inputStream = url.openStream()) {
            return evaluateInputStream(runtime, inputStream, url.getPath());
        }
    }

    /**
     * @param runtime
     * @param uri
     * @param path
     * @return
     * @throws IOException
     */
    public static Object evaluateURI(JavascriptRuntime runtime, URI uri, String path) throws IOException {
        return evaluateURL(runtime, uri.toURL());
    }

    /**
     * @param runtime
     * @param file
     * @return
     * @throws IOException
     */
    public static Object evaluateFile(JavascriptRuntime runtime, File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            return evaluateReader(runtime, reader, file.getPath());
        }
    }

}
