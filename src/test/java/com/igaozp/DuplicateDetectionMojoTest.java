package com.igaozp;

import com.igaozp.dsd.DuplicateDetectionMojo;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

class DuplicateDetectionMojoTest {

    @Test
    void testExecuteLogsStartup() throws Exception {
        DuplicateDetectionMojo mojo = new DuplicateDetectionMojo();
        Log mockLog = Mockito.mock(Log.class);
        mojo.setLog(mockLog);

        mojo.execute();

        verify(mockLog).info("Starting MyBatis Linter...");
    }
}
