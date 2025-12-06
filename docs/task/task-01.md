## Task 01 初始化项目

总体项目目标参考 task/global.md 文件

### 当前项目目标：项目初始化与基础架构构建

请根据当前文档描述完成本次任务

1.1 创建Maven Plugin项目

pom.xml 配置关键点：
```XML
<packaging>maven-plugin</packaging>
<name>MyBatis Duplicate SQL Linter</name>
<groupId>com.yourcompany.plugins</groupId>
<artifactId>mybatis-lint-maven-plugin</artifactId>
<version>1.0.0-SNAPSHOT</version>

<properties>
    <java.version>11</java.version>
    <maven.api.version>3.9.0</maven.api.version>
    <javaparser.version>3.25.5</javaparser.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.apache.maven</groupId>
        <artifactId>maven-plugin-api</artifactId>
        <version>${maven.api.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.maven.plugin-tools</groupId>
        <artifactId>maven-plugin-annotations</artifactId>
        <version>3.9.0</version>
        <scope>provided</scope>
    </dependency>
    
    <dependency>
        <groupId>com.github.javaparser</groupId>
        <artifactId>javaparser-core</artifactId>
        <version>${javaparser.version}</version>
    </dependency>
    
    <dependency>
        <groupId>dom4j</groupId>
        <artifactId>dom4j</artifactId>
        <version>2.1.3</version>
    </dependency>
    
    <dependency>
        <groupId>org.apache.maven.reporting</groupId>
        <artifactId>maven-reporting-impl</artifactId>
        <version>3.2.0</version>
    </dependency>
</dependencies>
```

1.2 定义Mojo入口
创建一个DuplicateDetectionMojo类。

```java
@Mojo(name = "check", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class DuplicateDetectionMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    // 配置项：是否在发现重复时中断构建
    @Parameter(property = "mybatis.lint.failOnError", defaultValue = "true")
    private boolean failOnError;

    // 配置项：排除的XML或Mapper路径
    @Parameter
    private List<String> excludes;

    public void execute() throws MojoExecutionException {
        getLog().info("Starting MyBatis Linter...");
        // 逻辑入口
    }
}
```

## 任务完成总结

### 1. 项目基础构建
- **Project Coordinates**: `com.igaozp.dsd:mybatis-lint-maven-plugin:1.0.0-SNAPSHOT`
- **Build Environment**: Java 8, Maven 3.9.0+
- **Dependencies**:
  - Maven Plugin API & Annotations
  - JavaParser (Core)
  - Maven Reporting Impl
  - Testing: JUnit 5, Mockito
  - *Note: `dom4j` 依赖已被暂时注释*

### 2. 核心组件
- **Mojo**: `com.igaozp.dsd.DuplicateDetectionMojo`
  - Defined goal: `check`
  - Parameters: `failOnError`, `excludes`
  - Phase: `PROCESS_SOURCES`

### 3. 测试验证
- 已创建基础单元测试 `DuplicateDetectionMojoTest`，验证 Mojo 启动日志。
