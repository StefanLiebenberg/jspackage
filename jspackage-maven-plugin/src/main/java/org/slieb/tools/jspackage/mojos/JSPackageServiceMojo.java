package org.slieb.tools.jspackage.mojos;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.kute.service.Service;

import java.io.IOException;

@Mojo(name = "service", defaultPhase = LifecyclePhase.NONE)
public class JSPackageServiceMojo extends AbstractPackageMojo {

    protected Service service;

    @Parameter
    public Integer port = 6655;

    public void start() throws InterruptedException {
        service = new Service(getSourceProvider(true), port);
        service.start();
    }

    public void stop() {
        service.stop();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            start();
            waitForKey();
        } catch (InterruptedException | IOException e) {
            throw new MojoFailureException("There was a failure in starting the service.", e);
        } finally {
            stop();
        }
    }

    private void waitForKey() throws IOException {
        info("Press any key to continue.");
        System.in.read();
    }

}




