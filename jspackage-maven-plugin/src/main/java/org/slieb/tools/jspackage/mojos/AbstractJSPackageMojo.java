package org.slieb.tools.jspackage.mojos;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.net.URI;
import java.util.List;

public abstract class AbstractJSPackageMojo extends AbstractMojo {
    
    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(required = true)
    protected List<URI> sources;

    @Parameter(required = true)
    protected List<String> inputs;

}
