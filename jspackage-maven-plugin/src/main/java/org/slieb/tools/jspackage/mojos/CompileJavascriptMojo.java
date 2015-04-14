package org.slieb.tools.jspackage.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


@Mojo(name = "compile", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true)
public class CompileJavascriptMojo extends AbstractMojo {

    @Parameter(required = true)
    private MavenProject project;


    public void execute() throws MojoExecutionException, MojoFailureException {
        throw new MojoExecutionException("Not implemented yet");
    }
}