package org.slieb.jspackage.container;

import com.google.template.soy.tofu.SoyTofu;
import org.slieb.kute.api.Resource;

import java.io.IOException;
import java.util.Map;

public interface DeployContainer {

    Resource.Provider getAssetsProvider();

    SoyTofu getTofu() throws IOException;

    Map<String, Object> getInformation(String configurationName);
}
