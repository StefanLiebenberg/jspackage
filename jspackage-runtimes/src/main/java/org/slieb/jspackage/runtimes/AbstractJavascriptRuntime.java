package org.slieb.jspackage.runtimes;


public abstract class AbstractJavascriptRuntime implements JavascriptRuntime {

    @Override
    public abstract Object execute(String command, String sourceName);
}
