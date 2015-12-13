package org.slieb.closure.javascript.internal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by stefan on 8/12/15.
 */
public class JsonPrinterTest {

    private JsonPrinter jsonPrinter;

    @Before
    public void setUp() throws Exception {

        jsonPrinter = new JsonPrinter();

    }

    @Test
    public void testPrintObjectMap() throws Exception {
        Assert.assertEquals("{\"key\": \"value\"}", jsonPrinter.printObjectMap(ImmutableMap.<String, Object>builder().put("key", "value").build()));
    }

    @Test
    public void testPrintStringMap() throws Exception {
        Assert.assertEquals("{\"key\": \"value\"}", jsonPrinter.printStringMap(ImmutableMap.<String, String>builder().put("key", "value").build()));
        Assert.assertEquals("{\"key2\": 2, \"key\": 1}", jsonPrinter.printStringMap(ImmutableSortedMap.<String, String>naturalOrder().put("key", "1").put("key2", "2").build()));
    }

    @Test
    public void testToValue() throws Exception {
        Assert.assertEquals("true", jsonPrinter.toValue(Boolean.TRUE));
        Assert.assertEquals("false", jsonPrinter.toValue(Boolean.FALSE));
        Assert.assertEquals("null", jsonPrinter.toValue(null));
        Assert.assertEquals("10", jsonPrinter.toValue(10));
        Assert.assertEquals("10.01", jsonPrinter.toValue(10.01));
        Assert.assertEquals("\"value\"", jsonPrinter.toValue("value"));
    }

    @Test
    public void testToObject() throws Exception {
        Assert.assertEquals(Boolean.TRUE, jsonPrinter.toObject("true"));
        Assert.assertEquals(Boolean.FALSE, jsonPrinter.toObject("false"));
        Assert.assertEquals(null, jsonPrinter.toObject("null"));
        Assert.assertEquals(10, jsonPrinter.toObject("10"));
        Assert.assertEquals(10.01, jsonPrinter.toObject("10.01"));
        Assert.assertEquals("value", jsonPrinter.toObject("value"));
    }
}