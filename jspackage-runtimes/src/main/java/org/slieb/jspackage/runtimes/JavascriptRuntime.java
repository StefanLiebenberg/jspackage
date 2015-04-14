package org.slieb.jspackage.runtimes;


/**
 * The JavaScript runtime wraps around runtimes and provides a common interfact to manage them.
 */
public interface JavascriptRuntime {

    default Object execute(String command) {
        return execute(command, ":inline");
    }

    Object execute(String command, String sourceName);

}
