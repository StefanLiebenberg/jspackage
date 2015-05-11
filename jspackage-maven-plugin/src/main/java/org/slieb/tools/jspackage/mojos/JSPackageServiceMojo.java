package org.slieb.tools.jspackage.mojos;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.jspackage.service.JSPackageConfiguration;
import org.slieb.jspackage.service.JSPackageService;

import java.io.File;
import java.util.List;

@Mojo(name = "service", defaultPhase = LifecyclePhase.TEST)
public class JSPackageServiceMojo extends AbstractPackageMojo {

    @Parameter(name = "testDirectories")
    protected List<File> testDirectories;

    protected JSPackageService service;

    public void start() throws InterruptedException {
        service = JSPackageService.create(
                new JSPackageConfiguration.Builder()
                        .withResourceProvider(getSourceResource(testDirectories))
                        .build());
        service.start();
    }

    public void stop() {
        service.stop();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            start();
            System.out.println("Press any key to continue.");
            System.in.read();
        } catch (Exception e) {
            throw new MojoFailureException("ioFailure", e);
        } finally {
            stop();
        }
    }
}
