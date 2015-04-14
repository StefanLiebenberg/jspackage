package org.slieb.jspackage.runtimes.rhino;


import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;
import org.slieb.jspackage.runtimes.JavascriptRuntime;

import java.io.Closeable;


public class RhinoRuntime implements Closeable, JavascriptRuntime {

    protected final Context context;

    protected final Global scope;

    public RhinoRuntime() {
        context = new ContextFactory().enterContext();
        scope = new Global(context);
    }

    public void initialize() {
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_3);
    }

    public Context getContext() {
        return context;
    }

    public Global getScope() {
        return scope;
    }

    public void close() {
        Context.exit();
    }


    public void putObject(String name, Object object) {
        ScriptableObject.putProperty(scope, name, object);
    }

    public Object getObject(String name) {
        return ScriptableObject.getProperty(scope, name);
    }

    public void putJavaObject(String name, Object object) {
        putObject(name, convertJavaObjectToJs(object));
    }

    public Object getJavaObject(String name) {
        return convertJsObjectToJava(getObject(name), Object.class);
    }

    public Function getFunction(String name) {
        return (Function) getObject(name);
    }

    public Object callFunction(String name, Scriptable thisObject, Object... args) {
        return getFunction(name).call(context, scope, thisObject, args);
    }

    public Object convertJavaObjectToJs(Object object) {
        return Context.javaToJS(object, scope);
    }

    public Object convertJsObjectToJava(Object object, Class<?> classType) {
        return Context.jsToJava(object, classType);
    }

    @Override
    public Object execute(String command, String sourceName) {


        return this.context.evaluateString(scope, command, sourceName, 0, null);
    }

}
