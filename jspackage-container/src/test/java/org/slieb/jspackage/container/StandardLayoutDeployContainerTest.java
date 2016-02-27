package org.slieb.jspackage.container;

import com.google.gson.Gson;
import com.google.template.soy.base.SoySyntaxException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slieb.jspackage.testresources.TestConstants;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;

public class StandardLayoutDeployContainerTest {

    public static final String SAMPLE_CONTENT = "sample content";
    public static final String ASSETS_SAMPLE_TXT = "/assets/sample.txt";

    @Test
    public void testTextAssets() throws IOException {
        Resource.Provider provider = Kute.providerOf(Kute.stringResource(ASSETS_SAMPLE_TXT, SAMPLE_CONTENT));
        StandardLayoutDeployContainer sldc = new StandardLayoutDeployContainer(provider);
        try (InputStream sampleInputStream = sldc.getResource("/sample.txt").get()) {
            Assert.assertEquals(SAMPLE_CONTENT, IOUtils.toString(sampleInputStream));
        }
    }

    @Test
    public void testTemplate() throws IOException {
        Resource.Readable templateResource = TestConstants.getResource("/templates/template.soy", "templates/ValidTemplates.soy");
        Resource.Provider provider = Kute.providerOf(templateResource);
        StandardLayoutDeployContainer sldc = new StandardLayoutDeployContainer(provider);
        Assert.assertEquals("one", sldc.getTofu().newRenderer("templates.One").render());
    }

    @Test(expected = SoySyntaxException.class)
    public void testTemplateWithError() throws IOException {
        Resource.Readable templateResource = TestConstants.getResource("/templates/template.soy", "templates/InvalidTemplates.soy");
        Resource.Provider provider = Kute.providerOf(templateResource);
        StandardLayoutDeployContainer sldc = new StandardLayoutDeployContainer(provider);
        sldc.getTofu();
    }

    @Test
    public void testGetInformation() throws IOException {
        final Gson gson = new Gson();
        final Resource.Readable templateResource = Kute.stringResource("/information/config.json", "{\"configured\": true}");
        final Resource.Provider provider = Kute.providerOf(templateResource);
        final StandardLayoutDeployContainer sldc = new StandardLayoutDeployContainer(provider);
        Assert.assertEquals(Optional.of(Boolean.TRUE),
                            sldc.getInformationResource("/config.json")
                                .map(castFunctionWithThrowable(IOUtils::toString))
                                .map(string -> gson.fromJson(string, Map.class).get("configured")));
    }
}