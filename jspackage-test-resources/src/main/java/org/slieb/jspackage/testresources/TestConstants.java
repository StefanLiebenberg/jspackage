package org.slieb.jspackage.testresources;

import org.slieb.kute.KuteFactory;
import org.slieb.kute.api.Resource;

import java.io.IOException;

public class TestConstants {

    public static String TXT_1_EXPECTED_PATH = "/txt/path1.txt";
    public static String TXT_1_EXPECTED_CONTENT = "Expected content in text file #1";

    public static String TXT_2_EXPECTED_PATH = "/txt/path2.txt";
    public static String TXT_2_EXPECTED_CONTENT = "Expected content in text file #2";

    public static Resource.Readable getResource(final String path,
                                                final String resourcePath) throws IOException {
        return KuteFactory.inputStreamResource(path, () -> {
            return TestConstants.class.getResourceAsStream(resourcePath);
        });
    }
}
