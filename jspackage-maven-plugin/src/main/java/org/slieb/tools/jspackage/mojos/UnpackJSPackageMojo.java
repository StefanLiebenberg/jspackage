package org.slieb.tools.jspackage.mojos;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor.*;

import java.io.File;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "unpack", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresProject = true)
public class UnpackJSPackageMojo extends AbstractMojo {


    @Component
    protected MavenProject project;

    @Component
    protected MavenSession session;

    @Component
    protected BuildPluginManager pluginManager;

    @Parameter(name = "outputDirectory", defaultValue = "${project.build.directory}/unpacked-dependencies")
    protected File outputDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("unpacking...");

        List<Dependency> unpackDeps = getUnpackDependencies();

        getLog().info("found " + unpackDeps.size() + " deps...");
        if (!unpackDeps.isEmpty()) {
            executeMojo(dependencyPlugin(), goal("unpack"), unpackConfiguration(), execEnv());
        }
    }

    private Plugin dependencyPlugin() {
        return plugin(groupId("org.apache.maven.plugins"), artifactId("maven-dependency-plugin"), version("2.8"));
    }

    private ExecutionEnvironment execEnv() {
        return new ExecutionEnvironment(project, session, pluginManager);
    }

    private Element getArtifactItem(Dependency dependency) {
        return element("artifact",
                element(name("groupId"), dependency.getGroupId()),
                element(name("artifactId"), dependency.getArtifactId()),
                element(name("version"), dependency.getVersion()),
                element(name("type"), dependency.getType()),
                element(name("classifier"), dependency.getClassifier()),
                element(name("includes"), "**/*.js"),
                element(name("excludes"), "**/*_test.js,**/alltests.js"),
                element(name("overWrite"), "true"));
    }


    private Xpp3Dom unpackConfiguration() {
        return configuration(
                element(name("artifactItems"), getUnpackArtifactItems()),
                element(name("outputDirectory"), outputDirectory.getPath()));
    }

    private Element[] getUnpackArtifactItems() {
        return getUnpackDependencies().stream().map(this::getArtifactItem).toArray(Element[]::new);
    }

    private List<Dependency> getUnpackDependencies() {
        return checkNotNull(project, "project cannot be null").getDependencies()
                .stream().filter(this::shouldUnpackDependency).collect(toList());
    }

    private Boolean shouldUnpackDependency(Dependency dependency) {
        getLog().info("attempting to unpack:"+dependency.getArtifactId()+":"+dependency.getType());
        switch (dependency.getType()) {
            case "jar":
            case "js-library":
                return true;
            default:
                return false;
        }
    }


}
