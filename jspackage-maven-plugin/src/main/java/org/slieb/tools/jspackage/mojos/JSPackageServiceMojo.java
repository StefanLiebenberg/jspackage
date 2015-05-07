package org.slieb.tools.jspackage.mojos;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.jspackage.service.JSPackageConfiguration;
import org.slieb.jspackage.service.JSPackageService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Mojo(name = "service", defaultPhase = LifecyclePhase.TEST)
public class JSPackageServiceMojo extends AbstractPackageMojo {

    @Parameter(name = "testDirectories")
    protected List<File> testDirectories;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        JSPackageService service = JSPackageService.create(
                new JSPackageConfiguration.Builder()
                        .withResourceProvider(getSourceResource(testDirectories))
                        .build());

        try {
            service.start();
            System.out.println("Press any key to continue.");
            System.in.read();
        } catch (InterruptedException e) {
            throw new MojoFailureException("service failed", e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            service.stop();
        }

    }
}
