package org.slieb.tools.jspackage.mojos;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slieb.kute.service.Service;

@Mojo(name = "service", defaultPhase = LifecyclePhase.TEST)
public class JSPackageServiceMojo extends AbstractPackageMojo {


    protected Service service;

    public void start() throws InterruptedException {

        service = new Service(getSourceProvider(true), 6655);
        service.start();
    }

    public void stop() {
        service.stop();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            start();
            info("Press any key to continue.");
            System.in.read();
        } catch (Exception e) {
            throw new MojoFailureException("ioFailure", e);
        } finally {
            stop();
        }
    }
}
