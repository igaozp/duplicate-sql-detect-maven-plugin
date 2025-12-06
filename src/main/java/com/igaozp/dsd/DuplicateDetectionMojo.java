package com.igaozp.dsd;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class DuplicateDetectionMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "mybatis.lint.failOnError", defaultValue = "true")
    private boolean failOnError;

    @Parameter
    private List<String> excludes;

    public void execute() throws MojoExecutionException {
        // Implementation pending
        getLog().info("Starting MyBatis Linter...");
    }
}
