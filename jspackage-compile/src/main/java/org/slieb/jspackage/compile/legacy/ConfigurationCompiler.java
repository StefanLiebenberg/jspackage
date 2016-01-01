package org.slieb.jspackage.compile.legacy;


import org.apache.commons.io.IOUtils;
import slieb.kute.api.Resource;

import java.io.*;

public class ConfigurationCompiler {

    public void compile(Configuration configuration, File outputDirectory) throws IOException {
        CompilerProvider provider = new CompilerProvider(configuration);
        for (Resource.Readable resource : provider) {
            File outputFile = new File(outputDirectory, resource.getPath());
            File parent = outputFile.getParentFile();
            if (parent.exists() || parent.mkdirs()) {
                try (Reader reader = resource.getReader();
                     Writer writer = new FileWriter(outputFile)) {
                    IOUtils.copy(reader, writer);
                }
            }
        }
    }

}
