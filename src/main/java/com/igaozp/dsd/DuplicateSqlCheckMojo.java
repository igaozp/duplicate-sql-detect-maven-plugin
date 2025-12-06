package com.igaozp.dsd;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

@Mojo(name = "check-duplicate-sql", defaultPhase = LifecyclePhase.VALIDATE, requiresProject = true, threadSafe = true)
public class DuplicateSqlCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(property = "sqlchecker.mapperLocations", defaultValue = "src/main/resources/mapper")
    private String mapperLocations;

    @Parameter(property = "sqlchecker.sourceDirectory", defaultValue = "${project.build.sourceDirectory}")
    private File sourceDirectory;

    @Parameter(property = "sqlchecker.failOnDuplicate", defaultValue = "true")
    private boolean failOnDuplicate;

    @Parameter(property = "sqlchecker.skip", defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("SQL 重复检测已跳过");
            return;
        }

        try {
            DuplicateReport report = runDetection();

            if (report.hasDuplicates() && failOnDuplicate) {
                throw new MojoFailureException(
                        String.format("检测到 %d 个重复 SQL 定义，构建中止！",
                                report.getDuplicateCount()));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Mapper 文件读取失败", e);
        }
    }

    protected DuplicateReport runDetection() throws IOException {
        // Placeholder for actual detection logic
        // For now, return a report with 0 duplicates to allow build to pass
        return new DuplicateReport(0);
    }
}
