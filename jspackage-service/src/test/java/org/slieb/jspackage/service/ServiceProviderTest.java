package org.slieb.jspackage.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slieb.jspackage.service.providers.ServiceProvider;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ServiceProviderTest {

    Resource.Readable resourceA, resourceB;
    ResourceProvider<Resource.Readable> readables;
    ServiceProvider provider;

    @Before
    public void setup() {
        resourceA = Resources.stringResource("/a_test.js", "a");
        resourceB = Resources.stringResource("/b_test.js", "b");
        readables = Resources.providerOf(resourceA, resourceB);
        provider = new ServiceProvider(readables);
    }


    private void assertLinkToResource(Resource.Readable resource, Element liElement) {
        assertNotNull(liElement);
        assertNotNull(liElement.child(0));
        assertEquals("A", liElement.child(0).tagName().toUpperCase());
        assertEquals(resource.getPath(), liElement.child(0).attr("href"));
    }

    @Test
    public void testIndex() throws IOException {
        Resource.Readable index = provider.getResourceByName("/");
        assertNotNull(index);
        Document document = Jsoup.parse(Resources.readResource(index));

        Elements items = document.getElementsByTag("li");
        assertLinkToResource(resourceA, items.get(0));
        assertLinkToResource(resourceB, items.get(1));
    }

    @Test
    public void testHtml() throws IOException {
        Resource.Readable testA = provider.getResourceByName("/a_test.html");
        assertNotNull(testA);
        Document document = Jsoup.parse(Resources.readResource(testA));
        Elements scripts = document.getElementsByTag("script");
    }
}