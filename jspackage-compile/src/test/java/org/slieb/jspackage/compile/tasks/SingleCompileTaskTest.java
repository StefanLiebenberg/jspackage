package org.slieb.jspackage.compile.tasks;

import com.google.javascript.jscomp.CompilerOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slieb.jspackage.compile.nodes.SingleCompileNode;
import org.slieb.jspackage.compile.result.CompileResult;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;

import java.util.Collections;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class SingleCompileTaskTest {

    private SingleCompileTask singleCompileTask;

    @Mock
    private SingleCompileNode compileNode;

    private Resource.Provider sources, externs;
    private CompilerOptions options;

    @Before
    public void setUp() throws Exception {

        options = new CompilerOptions();
        Mockito.when(compileNode.getCompilerOptions()).thenReturn(options);
        Mockito.when(compileNode.getSourcesProvider()).thenReturn(Kute.emptyProvider());
        Mockito.when(compileNode.getExternsProvider()).thenReturn(Kute.emptyProvider());
        Mockito.when(compileNode.getJsDefines()).thenReturn(Optional.empty());
        Mockito.when(compileNode.getCssRenameMap()).thenReturn(Optional.empty());
        Mockito.when(compileNode.getRequiredNamespaces()).thenReturn(Collections.emptySet());
        singleCompileTask = new SingleCompileTask();
    }

    @Test
    public void testCompileProducesNoExceptions() throws Exception {
        singleCompileTask.perform(compileNode);
    }

    @Test
    public void testCompileProducesOutput() throws Exception {
        final CompileResult result = singleCompileTask.perform(compileNode);
        Assert.assertEquals(result.getType(), CompileResult.Type.SUCCESS);
        CompileResult.Success success = (CompileResult.Success) result;
        Assert.assertNotNull(success.getCompiledResource());
    }
}