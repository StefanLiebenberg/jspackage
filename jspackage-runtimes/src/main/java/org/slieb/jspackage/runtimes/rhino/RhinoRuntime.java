package org.slieb.jspackage.runtimes.rhino;


import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.debugger.Main;
import org.mozilla.javascript.tools.shell.Global;
import org.slieb.jspackage.runtimes.JavascriptRuntime;

import java.io.Closeable;

import static org.mozilla.javascript.tools.debugger.Main.mainEmbedded;


public class RhinoRuntime implements Closeable, JavascriptRuntime {

    public static final Boolean DEBUG = !Boolean.valueOf(System.getProperty("rhino.debug", "false"));

    protected final ContextFactory contextFactory;

    protected final Context context;

    protected final Global scope;

    protected final Main mainWindow;

    public RhinoRuntime() {
        if (DEBUG) {
            contextFactory = org.mozilla.javascript.tools.shell.Main.shellContextFactory;
            context = contextFactory.enterContext();
            scope = org.mozilla.javascript.tools.shell.Main.getGlobal();
        } else {
            contextFactory = new ContextFactory();
            context = contextFactory.enterContext();
            scope = new Global(context);
        }

        if (DEBUG) {
            mainWindow = mainEmbedded(contextFactory, () -> scope, "debug window");
        } else {
            mainWindow = null;
        }
    }

    public void initialize() {
        contextFactory.call(context -> {
            context.setOptimizationLevel(-1);
            context.setLanguageVersion(Context.VERSION_DEFAULT);
            return null;
        });
    }

    public Context getContext() {
        return context;
    }

    public Global getScope() {
        return scope;
    }

    public void close() {
        if (DEBUG) {
            mainWindow.dispose();
        }
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
        return contextFactory.call(cx -> {
            Script script = cx.compileString(command, sourceName, 1, null);
            if (script != null) {
                return script.exec(cx, scope);
            } else {
                return null;
            }
        });
    }
//
    public static void main(String[] args) {

        RhinoRuntime runtime = new RhinoRuntime();
        runtime.initialize();
        runtime.execute("new Object();\n", "/path.js");

//
//        ContextFactory contextFactory = org.mozilla.javascript.tools.shell.Main.shellContextFactory;
//        Main main = Main.mainEmbedded(contextFactory, () -> global, "XXX");
////        main.setBreakOnExceptions(true);
////        main.setBreakOnEnter(true);
//
//        contextFactory.call(cx -> {
//            cx.setOptimizationLevel(-1);
//            cx.setLanguageVersion(Context.VERSION_DEFAULT);
//            cx.initStandardObjects(global);
//            Script script = cx.compileString("function red() {\n print('x'); \n}; \n", "<command 1>", 1, null);
//            if (script != null) {
//                System.out.println("running...");
//                return script.exec(cx, global);
//            } else {
//                return null;
//            }
//        });
//
//        main.doBreak();
//
//        contextFactory.call(cx -> {
//            Script script = cx.compileString("\n;red();\n", "<command 2>", 1, null);
//            if (script != null) {
//                System.out.println("running...");
//                return script.exec(cx, global);
//            } else {
//                return null;
//            }
//        });
//
//        System.out.println("done?");
    }

}
