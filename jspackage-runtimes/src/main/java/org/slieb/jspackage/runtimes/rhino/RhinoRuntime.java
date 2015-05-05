package org.slieb.jspackage.runtimes.rhino;


import org.apache.commons.io.output.TeeOutputStream;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.debugger.Main;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.ShellContextFactory;
import org.slieb.jspackage.runtimes.JavascriptRuntime;

import java.io.Closeable;
import java.io.PrintStream;

import static org.mozilla.javascript.tools.debugger.Main.mainEmbedded;


public class RhinoRuntime implements Closeable, JavascriptRuntime {

    public static final Boolean DEBUG = Boolean.valueOf(System.getProperty("rhino.debug", "false"));

    protected final ContextFactory contextFactory;

    protected final Global scope;

    protected final Main mainWindow;

    public RhinoRuntime() {
        scope = new Global();
        if (DEBUG) {
            contextFactory = new ShellContextFactory();
        } else {
            contextFactory = Context.enter().getFactory();
        }
        if (DEBUG) {
            mainWindow = mainEmbedded(contextFactory, scope, "Rhino Debug Window");
            mainWindow.setBreakOnExceptions(true);
            scope.setErr(new PrintStream(new TeeOutputStream(System.err, mainWindow.getErr())));
            scope.setOut(new PrintStream(new TeeOutputStream(System.out, mainWindow.getOut())));
            scope.setIn(mainWindow.getIn());
        } else {
            mainWindow = null;
        }

    }

    public void initialize() {
        contextFactory.call(context -> {
            context.setOptimizationLevel(-1);
            context.initStandardObjects(scope);
            context.addActivationName("base");
            scope.init(context);
            return null;
        });
    }

    public void close() {
        if (DEBUG) {
            mainWindow.dispose();
        } else {
            Context.exit();
        }
    }


    public void putObject(String name, Object object) {
        contextFactory.call(cx -> {
            ScriptableObject.putProperty(scope, name, object);
            return null;
        });
    }

    public Object getObject(String name) {
        return contextFactory.call(cx -> ScriptableObject.getProperty(scope, name));
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
        return contextFactory.call(cx -> getFunction(name).call(cx, scope, thisObject, args));
    }

    public Object convertJavaObjectToJs(Object object) {
        return Context.javaToJS(object, scope);
    }

    public Object convertJsObjectToJava(Object object, Class<?> classType) {
        return Context.jsToJava(object, classType);
    }

    @Override
    public Object execute(String command, String sourceName) {
        return contextFactory.call(cx -> {
            try {
                Script script = cx.compileString(command, sourceName, 1, null);
                if (script != null) {
                    return script.exec(cx, scope);
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
