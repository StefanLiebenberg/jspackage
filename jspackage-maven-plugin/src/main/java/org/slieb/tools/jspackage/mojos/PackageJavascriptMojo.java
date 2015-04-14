package org.slieb.tools.jspackage.mojos;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageJavascriptMojo extends AbstractJSPackageMojo {


    public void execute() throws MojoExecutionException, MojoFailureException {
        throw new Error("No Implemented");
    }
}
