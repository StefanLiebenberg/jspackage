package org.slieb.jspackage.container;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.tofu.SoyTofu;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slieb.kute.Kute;
import org.slieb.kute.KuteIO;
import org.slieb.kute.api.Resource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class StandardLayoutDeployContainerTest {

    private Injector injector;

    @Before
    public void setup() {
        injector = Guice.createInjector(new SoyModule());
    }

    @Test
    public void testTextAssets() throws IOException {
        Resource.Provider provider = Kute.providerOf(Kute.stringResource("/assets/sample.txt", "sample content"));
        StandardLayoutDeployContainer sldc = getStandardLayoutDeployContainer(provider);
        Resource.Provider assets = sldc.getAssetsProvider();
        Optional<Resource.Readable> optionalSample = assets.getResourceByName("/sample.txt");
        Assert.assertEquals("sample content", KuteIO.readResource(optionalSample.get()));
    }

    @Test
    public void testTemplate() throws IOException {
        Resource.Readable templateResource = Kute.stringResource("/templates/template.soy", "{namespace templates}\n\n/** */\n{template " +
                ".One}\none\n{/template}\n");
        Resource.Provider provider = Kute.providerOf(templateResource);
        StandardLayoutDeployContainer sldc = getStandardLayoutDeployContainer(provider);
        SoyTofu tofu = sldc.getTofu();
        Assert.assertEquals("one", tofu.newRenderer("templates.One").render());
    }

    @Test(expected = SoySyntaxException.class)
    public void testTemplateWithError() throws IOException {
        Resource.Readable templateResource = Kute.stringResource("/templates/template.soy", "{namespace templates}\n\n/** */\n{template " +
                ".One}\none {$Foo}\n{/template}\n");
        Resource.Provider provider = Kute.providerOf(templateResource);
        StandardLayoutDeployContainer sldc = getStandardLayoutDeployContainer(provider);
        sldc.getTofu();
    }

    @Test
    public void testGetInformation() throws IOException {
        Resource.Readable templateResource = Kute.stringResource("/information/config.json", "{\"configured\": true}");
        Resource.Provider provider = Kute.providerOf(templateResource);
        StandardLayoutDeployContainer sldc = getStandardLayoutDeployContainer(provider);
        Map<String, Object> config = sldc.getInformation("/config.json");
        Assert.assertEquals(Boolean.TRUE, config.get("configured"));
    }

    @Nonnull
    protected StandardLayoutDeployContainer getStandardLayoutDeployContainer(final Resource.Provider provider) {
        return new StandardLayoutDeployContainer(provider, injector.getProvider(SoyFileSet.Builder.class)::get);
    }
}