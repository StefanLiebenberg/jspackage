package org.slieb.jspackage.compile.providers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slieb.jspackage.compile.nodes.SingleCompileNode;
import org.slieb.jspackage.compile.resources.CompiledResource;
import org.slieb.jspackage.compile.resources.SourceMapResource;
import org.slieb.jspackage.compile.result.CompileResult;
import org.slieb.jspackage.compile.tasks.Task;
import org.slieb.kute.Kute;
import org.slieb.kute.KuteIO;
import org.slieb.kute.api.Resource;
import org.slieb.kute.resources.ContentResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@RunWith(MockitoJUnitRunner.class)
public class SingleCompileResourceProviderTest {

    @Mock
    Task<SingleCompileNode, CompileResult> mockCompileTask;

    @Mock
    SingleCompileNode singleCompileNode;

    @Mock
    CompileResult.Success mockSuccessResult;

    @Mock
    CompileResult.Failure mockFailureResult;

    @Mock
    SourceMapResource mockSourceMap;

    @Mock
    CompiledResource mockCompiledResource;

    SingleCompileResourceProvider provider;

    @Before
    public void setUp() throws Exception {
        Mockito.when(singleCompileNode.getSourcesProvider()).thenReturn(Kute.emptyProvider());
        Mockito.when(mockCompiledResource.getContent()).thenReturn("/** compiledResource **/");
        Mockito.when(mockCompiledResource.getPath()).thenReturn(":compiledContent");
        Mockito.when(mockCompiledResource.getInputStream()).thenCallRealMethod();
        Mockito.when(mockCompiledResource.getReader()).thenCallRealMethod();
        Mockito.when(mockSourceMap.getContent()).thenReturn("/** sourceMapResource **/");
        Mockito.when(mockSourceMap.getPath()).thenReturn(":sourceMap");
        Mockito.when(mockSourceMap.getInputStream()).thenCallRealMethod();
        Mockito.when(mockSourceMap.getReader()).thenCallRealMethod();

        Mockito.when(mockSuccessResult.getType()).thenReturn(CompileResult.Type.SUCCESS);
        Mockito.when(mockFailureResult.getType()).thenReturn(CompileResult.Type.FAILURE);
        Mockito.when(mockCompileTask.perform(Mockito.any())).thenReturn(mockFailureResult);
        Mockito.when(mockCompileTask.perform(singleCompileNode)).thenReturn(mockSuccessResult);
        Mockito.when(mockSuccessResult.getCompiledResource()).thenReturn(mockCompiledResource);
        Mockito.when(mockSuccessResult.getSourceMapResource()).thenReturn(mockSourceMap);
        provider = new SingleCompileResourceProvider(mockCompileTask, singleCompileNode);
    }

    @Test
    public void testProviderContainsTheSourceMap() throws Exception {
        Assert.assertTrue(provider.stream().anyMatch(this.resourceEquals(mockSourceMap)));
    }

    public Predicate<Resource.Readable> resourceEquals(Resource.Readable resource) {
        return (r) -> {
            try {
                return Objects.equals(r.getPath(), resource.getPath()) && Arrays.equals(KuteIO.toByteArray(r), KuteIO.toByteArray(resource));
            } catch (IOException e) {
                return false;
            }
        };
    }

    @Test
    public void testProviderContainsTheCompiledResource() throws Exception {
        Assert.assertTrue(provider.stream().anyMatch(this.resourceEquals(mockCompiledResource)));
    }

    @Test
    public void testDoesNotRecompileIfResourcesAreUnchanged() {
        Resource.Readable resourceA = Kute.stringResource("/a.js", "var a = {};");
        Resource.Readable resourceB = Kute.stringResource("/b.js", "var b = {};");
        Resource.Readable resourceC = Kute.stringResource("/c.js", "var c = {};");
        Resource.Provider sourceProvider = Kute.providerOf(resourceA, resourceB, resourceC);
        Mockito.when(singleCompileNode.getSourcesProvider()).thenReturn(sourceProvider);
        Assert.assertEquals(provider.stream().collect(toList()), provider.stream().collect(toList()));
        Mockito.verify(mockCompileTask, Mockito.times(1)).perform(singleCompileNode);
    }

    @Test
    public void testDoesRecompileAfterResourcesAreChanged() {
        MutableResource resourceA = new MutableResource("/a.js", "var a = {};");
        Resource.Readable resourceB = Kute.stringResource("/b.js", "var b = {};");
        Resource.Readable resourceC = Kute.stringResource("/c.js", "var c = {};");
        Resource.Provider sourceProvider = Kute.providerOf(resourceA, resourceB, resourceC);
        Mockito.when(singleCompileNode.getSourcesProvider()).thenReturn(sourceProvider);
        provider.stream().count();
        provider.stream().count();
        resourceA.setContent("var a = {'1':'2'};");
        provider.stream().count();
        provider.stream().count();
        Mockito.verify(mockCompileTask, Mockito.times(2)).perform(singleCompileNode);
    }
}

class MutableResource implements ContentResource {

    private String path, content;

    public MutableResource(String path,
                           String content) {
        this.path = path;
        this.content = content;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getContent() throws IOException {
        return content;
    }
}