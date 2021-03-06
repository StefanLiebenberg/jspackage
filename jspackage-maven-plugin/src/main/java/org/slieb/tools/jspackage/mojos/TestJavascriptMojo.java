package org.slieb.tools.jspackage.mojos;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slieb.jspackage.service.JSPackageConfigurationBuilder;
import org.slieb.jspackage.service.JSPackageService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Mojo(name = "component-tests", defaultPhase = LifecyclePhase.TEST)
public class TestJavascriptMojo extends AbstractPackageMojo {


    @Parameter(property = "skipTests", defaultValue = "${skipTests}")
    protected Boolean skipTests = false;


    @Parameter
    public List<File> componentTestSources;




    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (skipTests) return;

        JSPackageService service = JSPackageService.create(
                new JSPackageConfigurationBuilder()
                        .withResourceProvider(getSourceProvider(true, componentTestSources))
                        .build());

        try {
            URL base = new URL("http://localhost:6655");
            service.start();
            service.stream()
                    .filter(r -> r.getPath().endsWith("_test.html"))
                    .forEach(r -> {
                        try {
                            URL testUrl = new URL(base, r.getPath());
                            getLog().info("Running " + testUrl.toString());
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (InterruptedException | MalformedURLException e) {
            throw new MojoExecutionException("Failure", e);
        } finally {
            service.stop();
        }
    }
}
