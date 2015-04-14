package org.slieb.tools.jspackage.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST)
public class TestJavascriptMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
//        throw new MojoExecutionException("not implemented yet");
    }
}
