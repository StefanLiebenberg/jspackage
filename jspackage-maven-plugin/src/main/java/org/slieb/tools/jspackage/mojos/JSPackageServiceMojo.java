package org.slieb.tools.jspackage.mojos;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.jspackage.service.JSPackageConfiguration;
import org.slieb.jspackage.service.JSPackageService;
import org.slieb.kute.api.Resource;

import static org.slieb.jspackage.service.JSPackageConfigurationBuilder.aJSPackageConfiguration;

@Mojo(name = "service", defaultPhase = LifecyclePhase.NONE)
public class JSPackageServiceMojo extends AbstractPackageMojo {

    protected JSPackageService service;

    @Parameter
    public Integer port = 6655;

    public void start() throws InterruptedException {
        Resource.Provider resourceProvider = getSourceProvider(true);
        JSPackageConfiguration configuration = aJSPackageConfiguration().withPort(port).withResourceProvider(
                resourceProvider).build();
        service = new JSPackageService(configuration);
        service.start();
    }

    public void stop() {
        service.stop();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            start();
            waitForStop();
        } catch (InterruptedException e) {
            throw new MojoFailureException("There was a failure in starting the service.", e);
        } finally {
//            stop();
        }
    }

    private void waitForStop() throws InterruptedException {
        while (!service.stopped()) {
            Thread.sleep(1000);
        }
    }

}




