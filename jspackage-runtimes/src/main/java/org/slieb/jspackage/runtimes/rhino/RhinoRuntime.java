package org.slieb.jspackage.runtimes.rhino;


import org.apache.commons.io.output.TeeOutputStream;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.debugger.Main;
import org.mozilla.javascript.tools.shell.Global;
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
        if (DEBUG) {
            contextFactory = org.mozilla.javascript.tools.shell.Main.shellContextFactory;
            scope = org.mozilla.javascript.tools.shell.Main.getGlobal();
        } else {
            Context ctx = Context.enter();
            scope = new Global();
            contextFactory = ctx.getFactory();
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
            scope.init(context);
            context.setOptimizationLevel(-1);
            context.setLanguageVersion(Context.VERSION_DEFAULT);
            context.initStandardObjects(scope);
            return null;
        });
    }

//    public Context getContext() {
//        return context;
//    }

//    public Global getScope() {
//        return scope;
//    }

    public void close() {
        if (DEBUG) {
            mainWindow.dispose();
        } else {
            Context.exit();
        }
    }


    public void putObject(String name, Object object) {
        ScriptableObject.putProperty(scope, name, object);
    }

    public Object getObject(String name) {
        return ScriptableObject.getProperty(scope, name);
    }


//    public void putJavaObject(String name, Object object) {
//        putObject(name, convertJavaObjectToJs(object));
//    }

//    public Object getJavaObject(String name) {
//        return convertJsObjectToJava(getObject(name), Object.class);
//    }

//    public Function getFunction(String name) {
//        return (Function) getObject(name);
//    }

//    public Object callFunction(String name, Scriptable thisObject, Object... args) {
//        return getFunction(name).call(context, scope, thisObject, args);
//    }

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
