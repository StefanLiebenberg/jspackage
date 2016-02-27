package org.slieb.jspackage.container;

import com.google.template.soy.tofu.SoyTofu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface DeployContainer {

    Optional<InputStream> getResource(String path) throws IOException;

    Optional<InputStream> getInformationResource(String path) throws IOException;

    SoyTofu getTofu() throws IOException;
}
