package org.slieb.jspackage.runtimes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.slieb.jspackage.runtimes.JavascriptRuntimeUtils.*;

@RunWith(MockitoJUnitRunner.class)
public class JavascriptRuntimeUtilsTest {

    @Mock
    AbstractJavascriptRuntime mockRuntime;

    @Before
    public void setup() {
        when(mockRuntime.execute(anyString())).thenCallRealMethod();
        when(mockRuntime.execute(eq("TRUE"), anyString())).thenReturn(Boolean.TRUE);
        when(mockRuntime.execute(eq("FALSE"), anyString())).thenReturn(Boolean.FALSE);
        when(mockRuntime.execute(eq("NULL"), anyString())).thenReturn(null);
        when(mockRuntime.execute(eq("OBJECT"), anyString())).thenReturn(new Object());
        when(mockRuntime.execute(eq("STRING"), anyString())).thenReturn("some string");
        when(mockRuntime.execute(eq("INTEGER"), anyString())).thenReturn(1024);
    }

    @Test
    public void testGetTrueBooleanFromJsRuntime() throws Exception {
        assertTrue(getBooleanFromJsRuntime(mockRuntime, "TRUE"));
    }

    @Test
    public void testGetFalseBooleanFromJsRuntime() throws Exception {
        assertFalse(getBooleanFromJsRuntime(mockRuntime, "FALSE"));
    }

    @Test
    public void testGetNullBooleanFromJsRuntime() throws Exception {
        assertNull(getBooleanFromJsRuntime(mockRuntime, "NULL"));
    }

    @Test(expected = ClassCastException.class)
    public void testGetObjectBooleanFromJsRuntime() throws Exception {
        getBooleanFromJsRuntime(mockRuntime, "OBJECT");
    }

    @Test(expected = ClassCastException.class)
    public void testGetStrictBooleanFromJsRuntime() throws Exception {
        getBooleanFromJsRuntime(mockRuntime, "STRING");
    }

    @Test
    public void testGetStringFromJsRuntime() throws Exception {
        assertEquals("some string", getStringFromJsRuntime(mockRuntime, "STRING"));
    }

    @Test
    public void testGetIntegerFromJsRuntime() throws Exception {
        assertEquals(Integer.valueOf(1024), getIntegerFromJsRuntime(mockRuntime, "INTEGER"));
    }

    @Test
    public void testEvaluateReader() throws Exception {
        try (Reader reader = new StringReader("FALSE")) {
            assertFalse((Boolean) evaluateReader(mockRuntime, reader, ":inline"));
        }
    }
    
}