package org.slieb.jspackage.runtimes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JavascriptRuntimeTest {

    @Mock
    public AbstractJavascriptRuntime mockRuntime;

    @Test
    public void testExecute() throws Exception {
        when(mockRuntime.execute(anyString())).thenCallRealMethod();
        mockRuntime.execute(anyString());
        verify(mockRuntime, times(1)).execute(anyString());
        verify(mockRuntime, times(1)).execute(anyString(), eq(":inline"));
        verifyNoMoreInteractions(mockRuntime);
    }
}