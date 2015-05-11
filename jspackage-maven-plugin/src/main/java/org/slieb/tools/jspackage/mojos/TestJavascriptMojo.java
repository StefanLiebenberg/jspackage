package org.slieb.tools.jspackage.mojos;

import com.betgenius.selenium.DriverProvider;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slieb.jspackage.service.JSPackageConfiguration.Builder;
import org.slieb.jspackage.service.JSPackageService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

@Mojo(name = "component-tests", defaultPhase = LifecyclePhase.TEST)
public class TestJavascriptMojo extends AbstractPackageMojo {

    @Parameter
    public List<File> componentTestSources;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        JSPackageService service = JSPackageService.create(new Builder()
                .withResourceProvider(getSourceResource(componentTestSources))
                .build());

        RemoteWebDriver remoteWebDriver = DriverProvider.getSystemConfigDriverProvider().getRemoteWebDriver();
        try {
            URL base = new URL("http://localhost:6655");
            service.start();

            remoteWebDriver.get(base.toString());

            service.stream()
                    .filter(r -> r.getPath().endsWith("_test.html"))
                    .forEach(r -> {
                        try {
                            URL testUrl = new URL(base, r.getPath());
                            getLog().info("Running " + testUrl.toString());
                            remoteWebDriver.get(testUrl.toString());
                            Thread.sleep(10000);
                        } catch (InterruptedException | MalformedURLException e) {
                            throw new RuntimeException(e);
                        }

                    });

            Thread.sleep(30000);
        } catch (InterruptedException | MalformedURLException e) {
            throw new MojoExecutionException("Failure", e);
        } finally {
            remoteWebDriver.quit();
            service.stop();
        }
    }
}
