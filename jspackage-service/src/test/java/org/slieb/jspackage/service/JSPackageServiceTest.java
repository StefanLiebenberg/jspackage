package org.slieb.jspackage.service;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static slieb.kute.resources.Resources.readResource;


public class JSPackageServiceTest {

    static Resource.Readable readableA, readableB, readableC;

    static ResourceProvider<Resource.Readable> provider;

    static JSPackageConfiguration configuration;

    static JSPackageService service;

    static URL baseUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        readableA = Resources.stringResource("/a.js", "var a = 'A';");
        readableB = Resources.stringResource("/b.js", "var b = 'B';");
        readableC = Resources.stringResource("/nested/c_test.js", "var c = 'C';");
        provider = Resources.providerOf(readableA, readableB, readableC);

        configuration = new JSPackageConfiguration.Builder()
                .withResourceProvider(provider)
                .build();
        service = JSPackageService.create(configuration);
        service.start();
        baseUrl = new URL("http://localhost:" + configuration.getPort().toString());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service.stop();
    }

    public static void main(String[] args) throws Exception {
        setUp();
    }

    @Test
    public void testPort() throws Exception {
        baseUrl.openConnection().connect();
    }

    @Test
    public void testResourcesProvided() throws Exception {
        provider.stream()
                .parallel()
                .forEach(resource -> {
                    try {
                        URL url = new URL(baseUrl, resource.getPath());
                        try (InputStream inputStream = url.openStream()) {
                            assertEquals(readResource(resource), IOUtils.toString(inputStream));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testIndexing() throws IOException, InterruptedException {
        try (InputStream inputStream = baseUrl.openStream()) {
            Document index = Jsoup.parse(inputStream, Charset.defaultCharset().name(), baseUrl.toString());
            Elements links = index.getElementsByTag("a");
            assertEquals(6, links.size());
            assertEquals("/nested", links.get(0).attr("href"));
            assertEquals("/a.js", links.get(1).attr("href"));
            assertEquals("/b.js", links.get(2).attr("href"));
            assertEquals("/cssRenameMap.js", links.get(3).attr("href"));
            assertEquals("/defines.js", links.get(4).attr("href"));
            assertEquals("/deps.js", links.get(5).attr("href"));
        }

        try (InputStream inputStream = new URL(baseUrl, "/nested").openStream()) {
            Document index = Jsoup.parse(inputStream, Charset.defaultCharset().name(), baseUrl.toString());
            Elements links = index.getElementsByTag("a");
            assertEquals(2, links.size());
            assertEquals("/nested/c_test.html", links.get(0).attr("href"));
            assertEquals("/nested/c_test.js", links.get(1).attr("href"));
        }
    }

}