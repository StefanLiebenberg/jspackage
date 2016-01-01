package org.slieb.jspackage.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.slieb.jspackage.service.providers.ServiceProvider;
import slieb.kute.Kute;
import slieb.kute.KuteIO;
import slieb.kute.api.Resource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;


public class ServiceProviderTest {

    Resource.Readable resourceA, resourceB;
    Resource.Provider readables;
    ServiceProvider provider;

    @Before
    public void setup() {
        resourceA = Kute.stringResource("/a_test.js", "a");
        resourceB = Kute.stringResource("/b_test.js", "b");
        readables = Kute.providerOf(resourceA, resourceB);
        provider = new ServiceProvider(readables);
    }


    private void assertLinkToResource(Resource.Readable resource,
                                      Element liElement) {
        assertNotNull(liElement);
        assertNotNull(liElement.child(0));
        assertEquals("A", liElement.child(0).tagName().toUpperCase());
        assertEquals(resource.getPath(), liElement.child(0).attr("href"));
    }

    @Test
    public void testIndex() throws IOException {
        Optional<Resource.Readable> optionalIndex = provider.getResourceByName("/");
        assertTrue(optionalIndex.isPresent());
        Resource.Readable index = optionalIndex.get();
        Document document = Jsoup.parse(KuteIO.readResource(index));
        Elements items = document.getElementsByTag("li");
        assertLinkToResource(resourceA, items.get(3));
        assertLinkToResource(resourceB, items.get(5));
    }

//    @Test
//    public void testHtml() throws IOException {
//        Resource.Readable testA = provider.getResourceByName("/a_test.html").get();
//        Document document = Jsoup.parse(KuteIO.readResource(testA));
//        Elements scripts = document.getElementsByTag("script");
//    }
}