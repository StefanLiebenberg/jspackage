package org.slieb.jspackage.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slieb.jspackage.service.resources.IndexResource;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static slieb.kute.resources.Resources.providerOf;
import static slieb.kute.resources.Resources.readResource;


public class IndexResourceTest {

    Resource.Readable resourceA, resourceB;

    IndexResource indexResource;

    @Before
    public void setup() {
        resourceA = Resources.stringResource("/a.js", "a");
        resourceB = Resources.stringResource("/b.js", "b");
        ResourceProvider<Resource.Readable> resources = providerOf(resourceA, resourceB);
        indexResource = new IndexResource("/", resources);
    }

    @Test
    public void testEntries() throws IOException {
        Document document = Jsoup.parse(readResource(indexResource));
        Elements items = document.getElementsByTag("li");

        assertEquals(2, items.size());

        Element linkA = items.get(0);
        assertNotNull(linkA);
        assertNotNull(linkA.child(0));
        assertEquals("A", linkA.child(0).tagName().toUpperCase());
        assertEquals(resourceA.getPath(), linkA.child(0).attr("href"));

        Element linkB = items.get(1);
        assertNotNull(linkB);
        assertNotNull(linkB.child(0));
        assertEquals("A", linkB.child(0).tagName().toUpperCase());
        assertEquals(resourceB.getPath(), linkB.child(0).attr("href"));
    }


}