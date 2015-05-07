package org.slieb.jspackage.runtimes.rhino;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.slieb.jspackage.runtimes.JavascriptRuntimeUtils.getIntegerFromJsRuntime;
import static org.slieb.jspackage.runtimes.JavascriptRuntimeUtils.getStringFromJsRuntime;


public class EnvJSRuntimeTest {

    EnvJSRuntime runtime;

    @Before
    public void setupRuntime() {
        runtime = new EnvJSRuntime();
    }

    @After
    public void tearDownRuntime() {
        runtime.close();
    }

    @Test
    public void testRuntimeInitializesWithoutIssue() throws Exception {
        runtime.initialize();
    }

    public void testRuntimeInitializeAndLoadsWithoutIssue() throws Exception {
        runtime.initialize();
        runtime.doLoad();
    }

    @Test
    public void testSetTimeout() {
        runtime.initialize();

        runtime.execute("var value = \"A\";");

        runtime.execute("window.setTimeout(function(){value=\"B\";}, 100);");
        runtime.doWait();

        assertEquals("B", getStringFromJsRuntime(runtime, "value"));
    }

    @Test
    public void testClearTimeout() throws Throwable {
        runtime.initialize();

        runtime.execute("var value = \"A\";");

        runtime.execute("var x = window.setTimeout(function(){value=\"B\";}, 100);");
        runtime.execute("window.clearTimeout(x);");
        runtime.doWait();

        assertEquals("A", getStringFromJsRuntime(runtime, "value"));
    }

    @Test
    public void testIntervals() throws Throwable {
        runtime.initialize();
        runtime.execute("var value = 0;");
        runtime.execute("window.setInterval(function(){ value++; }, 1000);");

        runtime.doWait(1000);
        assertEquals((Integer) 1, getIntegerFromJsRuntime(runtime, "value"));

        runtime.doWait(1000);
        assertEquals((Integer) 2, getIntegerFromJsRuntime(runtime, "value"));
    }

}