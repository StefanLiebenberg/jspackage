package org.slieb.jspackage.service;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slieb.jspackage.dependencies.GoogResources;
import org.slieb.jspackage.service.resources.ComponentTestResource;
import org.slieb.kute.KuteFactory;
import org.slieb.kute.api.Resource;

import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.slieb.kute.Kute.providerOf;
import static org.slieb.kute.Kute.stringResource;

public class ComponentTestResourceTest {

    Resource.Readable resourceBase, resourceA, resourceB;

    ComponentTestResource resource;

    @Before
    public void setup() {
        resourceBase = KuteFactory.inputStreamResource("/base.js", () -> getClass().getResourceAsStream(
                "/closure-library/closure/goog/base.js"));
        resourceA = stringResource("/a.js", "goog.provide('a'); goog.require('b');");
        resourceB = stringResource("/b.js", "goog.provide('b');");
        resource = new ComponentTestResource("/test.html", resourceA, GoogResources.getCalculator(
                providerOf(resourceBase, resourceA, resourceB)));
    }

    @Test
    public void testGetReader() throws Exception {
        Reader reader = resource.getReader();
        assertNotNull(reader);
        Document document = Jsoup.parse(IOUtils.toString(reader));
        assertNotNull(document);
        Element head = document.getElementsByTag("head").first();
        assertNotNull(head);
        Elements scripts = document.getElementsByTag("script");
        assertEquals(3, scripts.size());

        Element scriptBase = scripts.get(0);
        assertNotNull(scriptBase);
        assertEquals("/base.js", scriptBase.attr("src"));

        Element scriptB = scripts.get(1);
        assertNotNull(scriptB);
        assertEquals("/b.js", scriptB.attr("src"));

        Element scriptA = scripts.get(2);
        assertNotNull(scriptA);
        assertEquals("/a.js", scriptA.attr("src"));
    }

    @Test
    public void testGetPath() throws Exception {
        String path = resource.getPath();
        assertNotNull(path);
        assertEquals("/test.html", path);
    }
}