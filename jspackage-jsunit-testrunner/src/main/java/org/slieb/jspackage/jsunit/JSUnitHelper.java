package org.slieb.jspackage.jsunit;


import org.slieb.jspackage.runtimes.JavascriptRuntime;

import static java.lang.String.format;
import static org.slieb.jspackage.runtimes.JavascriptRuntimeUtils.*;

public class JSUnitHelper {
    
    private JSUnitHelper() {
    }

    /**
     * Command constants used.
     */
    private static final String
            TEST_RUNNER = "G_testRunner",
            INITIALIZE = "%s.initialize();",
            IS_INITIALIZED = "%s.isInitialized();",
            IS_FINISHED = "%s.isFinished();",
            IS_SUCCESS = "%s.isSuccess();",
            GET_REPORT = "%s.getReport();",
            GET_RUNTIME = "%s.getRuntime();",
            GET_NUM_FILES_LOADED = "%s.getNumFilesLoaded();",
            SET_STRICT = "%s.setStrict(%s);",
            LOG_TEST_FAILURES = "%s.logTestFailures();",
            GET_TEST_RESULTS = "%s.getTestResults();";


    public static void initialize(JavascriptRuntime runtime) {
        runtime.execute(format(INITIALIZE, TEST_RUNNER));
    }

    public static Boolean isInitialized(JavascriptRuntime runtime) {
        return getBooleanFromJsRuntime(runtime, format(IS_INITIALIZED, TEST_RUNNER));
    }

    public static Boolean isFinished(JavascriptRuntime runtime) {
        return getBooleanFromJsRuntime(runtime, format(IS_FINISHED, TEST_RUNNER));
    }

    public static Boolean isSuccess(JavascriptRuntime runtime) {
        return getBooleanFromJsRuntime(runtime, format(IS_SUCCESS, TEST_RUNNER));
    }

    public static String getReport(JavascriptRuntime runtime) {
        return getStringFromJsRuntime(runtime, format(GET_REPORT, TEST_RUNNER));
    }

    public static Integer getRuntime(JavascriptRuntime runtime) {
        return getIntegerFromJsRuntime(runtime, format(GET_RUNTIME, TEST_RUNNER));
    }

    public static Integer getNumFilesLoaded(JavascriptRuntime runtime) {
        return getIntegerFromJsRuntime(runtime, format(GET_NUM_FILES_LOADED, TEST_RUNNER));
    }

    public static Object getTestResults(JavascriptRuntime runtime) {
        return runtime.execute(format(GET_TEST_RESULTS, TEST_RUNNER));
    }

    public static void setStrict(JavascriptRuntime runtime, Boolean strict) {
        runtime.execute(format(SET_STRICT, TEST_RUNNER, strict));
    }
}
