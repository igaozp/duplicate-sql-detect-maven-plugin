# Task 02 

总体项目目标参考 task/global.md 文件

## 当前项目目标：

请根据当前文档描述完成本次任务:
1. 根据示例代码，优化核心处理逻辑

任务完成后请在当前文件后面添加任务的完成情况

### Mojo 核心实现
Maven 插件的核心是 Mojo（Maven plain Old Java Object），通过 @Mojo 注解定义 Goal 名称和执行阶段。对于 SQL 检测插件，推荐绑定到 validate 阶段，在编译前尽早发现问题：
```java
@Mojo(
name = "check-duplicate-sql",
defaultPhase = LifecyclePhase.VALIDATE,
requiresProject = true,
threadSafe = true
)
public class DuplicateSqlCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(property = "sqlchecker.mapperLocations",
               defaultValue = "src/main/resources/mapper")
    private String mapperLocations;

    @Parameter(property = "sqlchecker.sourceDirectory",
               defaultValue = "${project.build.sourceDirectory}")
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
}
```

关键异常处理机制：MojoFailureException 用于预期的检测失败（如发现重复），会显示 BUILD FAILURE；MojoExecutionException 用于意外错误（如 IO 异常），会显示 BUILD ERROR。

## 任务完成情况

- [x] 根据示例代码，优化核心处理逻辑
- [x] 创建 DuplicateReport 类
- [x] 重构 DuplicateSqlCheckMojo 类
- [x] 实现 execute 方法逻辑
- [x] 验证代码结构
