package org.slieb.jspackage.container;

import com.google.template.soy.tofu.SoyTofu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface DeployContainer {

    Optional<InputStream> getAssetInputStream(String path) throws IOException;

    Optional<SoyTofu> getTofu() throws IOException;

    Optional<Map<String, Object>> getInformation(String configurationName);
}
