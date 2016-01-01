package org.slieb.jspackage.compile;

import com.google.inject.Provider;
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
import org.slieb.jspackage.compile.tasks.ModuleCompileTask;
import org.slieb.jspackage.compile.tasks.SingleCompileTask;
import slieb.kute.Kute;
import slieb.kute.KutePredicates;
import slieb.kute.api.Resource;

import java.io.IOException;


@RunWith(MockitoJUnitRunner.class)
public class CompilesProviderFactorySingleCompilesTest {

    @Mock
    private Provider<ModuleCompileTask> mockModuleTaskProvider;

    @Mock
    private Provider<SingleCompileTask> mockSingleTaskProvider;

    @Mock
    private SingleCompileTask mockSingleCompileTask;

    CompilesProviderFactory factory;

    @Before
    public void setUp() throws Exception {
        Mockito.when(mockSingleTaskProvider.get()).thenReturn(mockSingleCompileTask);
        factory = new CompilesProviderFactory(mockSingleTaskProvider);
    }

    @Test
    public void shouldCreateSingleCompile() throws Exception {
        SingleCompileNode singleCompileNode = Mockito.mock(SingleCompileNode.class);
        Mockito.when(singleCompileNode.getSourcesProvider()).thenReturn(Kute.emptyProvider());
        CompileResult.Success success = Mockito.mock(CompileResult.Success.class);
        Mockito.when(success.getType()).thenReturn(CompileResult.Type.SUCCESS);
        Mockito.when(mockSingleCompileTask.perform(singleCompileNode)).thenReturn(success);


        CompiledResource compiled = Mockito.mock(CompiledResource.class);
        Mockito.when(compiled.getContent()).thenReturn("compiled");
        Mockito.when(compiled.getInputStream()).thenCallRealMethod();
        Mockito.when(compiled.getReader()).thenCallRealMethod();
        Mockito.when(success.getCompiledResource()).thenReturn(compiled);

        SourceMapResource sourceMap = Mockito.mock(SourceMapResource.class);
        Mockito.when(sourceMap.getContent()).thenReturn("source map");
        Mockito.when(sourceMap.getInputStream()).thenCallRealMethod();
        Mockito.when(sourceMap.getReader()).thenCallRealMethod();
        Mockito.when(success.getSourceMapResource()).thenReturn(sourceMap);

        Resource.Provider provider = factory.createSingeCompileProvider(singleCompileNode);
        Assert.assertEquals(2, provider.stream().count());
        Assert.assertTrue(provider.stream().anyMatch(r -> {
            try {
                return KutePredicates.resourceEquals(r, compiled);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        Assert.assertTrue(provider.stream().anyMatch(r -> {
            try {
                return KutePredicates.resourceEquals(r, sourceMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Test
    public void shouldCreateSingleCompileFailure() throws Exception {
        SingleCompileNode singleNode = Mockito.mock(SingleCompileNode.class);
        Mockito.when(singleNode.getSourcesProvider()).thenReturn(Kute.emptyProvider());
        CompileResult.Failure mockResult = Mockito.mock(CompileResult.Failure.class);
        Mockito.when(mockSingleCompileTask.perform(singleNode)).thenReturn(mockResult);
        Mockito.when(mockResult.getType()).thenReturn(CompileResult.Type.FAILURE);
        Resource.Provider provder = factory.createSingeCompileProvider(singleNode);
        Assert.assertEquals(0, provder.stream().count());
    }

    @Test
    public void shouldCacheResults() throws IOException {
        SingleCompileNode singleCompileNode = Mockito.mock(SingleCompileNode.class);
        Mockito.when(singleCompileNode.getSourcesProvider()).thenReturn(Kute.emptyProvider());
        CompileResult.Success success = Mockito.mock(CompileResult.Success.class);
        Mockito.when(success.getType()).thenReturn(CompileResult.Type.SUCCESS);
        Mockito.when(mockSingleCompileTask.perform(singleCompileNode)).thenReturn(success);


        CompiledResource compiled = Mockito.mock(CompiledResource.class);
        Mockito.when(compiled.getContent()).thenReturn("compiled");
        Mockito.when(compiled.getInputStream()).thenCallRealMethod();
        Mockito.when(compiled.getReader()).thenCallRealMethod();
        Mockito.when(success.getCompiledResource()).thenReturn(compiled);

        SourceMapResource sourceMap = Mockito.mock(SourceMapResource.class);
        Mockito.when(sourceMap.getContent()).thenReturn("source map");
        Mockito.when(sourceMap.getInputStream()).thenCallRealMethod();
        Mockito.when(sourceMap.getReader()).thenCallRealMethod();
        Mockito.when(success.getSourceMapResource()).thenReturn(sourceMap);

        Resource.Provider provider = factory.createCachedSingleCompileProvider(singleCompileNode);


        provider.stream().count();
        provider.stream().count();
        provider.stream().count();
        provider.stream().count();
        provider.stream().count();

// verify compile task only called once.
    }
}