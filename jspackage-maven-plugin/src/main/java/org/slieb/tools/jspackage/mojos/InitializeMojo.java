package org.slieb.tools.jspackage.mojos;

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "initialize",
        defaultPhase = LifecyclePhase.INITIALIZE,
        requiresProject = true)
public class InitializeMojo extends AbstractMojo {

    @Parameter(required = true, readonly = true, defaultValue = "${project}")
    public MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Build build = project.getBuild();
        build.setSourceDirectory("src/main/javascript");
        build.setTestSourceDirectory("src/test/javascript");

        List<Resource> resources = project.getResources();
        while (!resources.isEmpty()) {
            resources.remove(0);
        }
        Resource sourceResource = new Resource();
        sourceResource.setDirectory(build.getSourceDirectory());
        resources.add(sourceResource);

        List<Resource> testResources = project.getTestResources();
        while (!testResources.isEmpty()) {
            testResources.remove(0);
        }
        Resource testResource = new Resource();
        testResource.setDirectory(build.getTestSourceDirectory());
        testResources.add(testResource);


        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-dependency-plugin");
        plugin.setVersion("2.8");

        project.getDependencies().stream()
                .filter(dependency -> dependency.getType().equals("jar"))
                .forEach(dependency -> {
                    PluginExecution exec = new PluginExecution();
                    exec.setPhase("generated-resources");
                    exec.addGoal("unpack");
                    exec.setConfiguration(configuration(
                            element(name("artifactItems"),
                                    element(name("artifactItem"),
                                            element(name("groupId"), dependency.getGroupId()),
                                            element(name("artifactId"), dependency.getArtifactId()),
                                            element(name("version"), dependency.getVersion()),
                                            element(name("classifier"), dependency.getClassifier()),
                                            element(name("overWrite"), "true")
                                    )
                            ),
                            element(name("outputDirectory"), "target/unpacked-dependencies")));
                    plugin.addExecution(exec);
                });

        build.addPlugin(plugin);
    }
}
