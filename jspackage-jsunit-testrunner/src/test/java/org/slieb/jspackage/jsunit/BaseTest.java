package org.slieb.jspackage.jsunit;


import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Undefined;
import org.slieb.runtimes.rhino.RhinoRuntime;

public class BaseTest {

    /**
     * Create a goog.base method and check awesomeness.
     */
    @Test
    public void testBase() {
        try (RhinoRuntime runtime = new RhinoRuntime()) {
            runtime.initialize();
            runtime.execute("var goog = { base : function () { return arguments.callee.caller; } };");
            runtime.execute("function Y () { return goog.base(); } ");
            Object result = runtime.execute("Y()");
            Assert.assertNotNull(result);
            Assert.assertNotEquals(Undefined.instance, result);
        }
    }
}
