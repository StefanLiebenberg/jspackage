package org.slieb.jspackage.jsunit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slieb.jspackage.runtimes.JavascriptRuntime;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.slieb.jspackage.jsunit.JSUnitHelper.*;

@RunWith(MockitoJUnitRunner.class)
public class JSUnitHelperTest {

    @Mock
    private JavascriptRuntime mockRuntime;

    private JSUnitHelper jsUnitHelper;


    @Before
    public void setup() {
        when(mockRuntime.execute(anyString(), anyString())).thenReturn(null);
        when(mockRuntime.execute(anyString())).thenReturn(null);
    }

    @Test
    public void testInitialize() throws Exception {
        initialize(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.initialize();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testIsInitialized() throws Exception {
        isInitialized(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.isInitialized();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testIsFinished() throws Exception {
        isFinished(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.isFinished();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testIsSuccess() throws Exception {
        isSuccess(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.isSuccess();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testGetReport() throws Exception {
        getReport(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.getReport();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testGetRuntime() throws Exception {
        getRuntime(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.getRuntime();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testGetNumFilesLoaded() throws Exception {
        getNumFilesLoaded(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.getNumFilesLoaded();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testGetTestResults() throws Exception {
        getTestResults(mockRuntime);
        verify(mockRuntime, times(1)).execute("G_testRunner.getTestResults();");
        verifyNoMoreInteractions(mockRuntime);
    }

    @Test
    public void testSetStrict() throws Exception {
        setStrict(mockRuntime, true);
        verify(mockRuntime, times(1)).execute("G_testRunner.setStrict(true);");
        setStrict(mockRuntime, false);
        verify(mockRuntime, times(1)).execute("G_testRunner.setStrict(false);");
        verifyNoMoreInteractions(mockRuntime);
    }
}