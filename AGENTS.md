# Repository Guidelines

## 项目结构与模块组织
- Maven 插件布局：生产代码在 `src/main/java`，测试在 `src/test/java`；如需资源文件放在对应 `resources` 目录。
- 文档位于 `docs/`（`docs/rules/tdd.md` 规定 TDD 循环，`docs/task/*.md` 说明任务范围与功能期望）。
- `pom.xml` 使用 `maven-plugin` 打包，目标 Java 8，依赖 Maven Plugin API/Annotations 与 JavaParser 进行 Java 源码分析。

## 构建、测试与开发命令
- 每次 Maven 命令都使用团队提供的 `settings.xml` 访问内部仓库：设置 `SETTINGS=/absolute/path/to/settings.xml`。
- 全量构建 + 测试：`mvn --settings $SETTINGS clean verify`
- 仅运行测试：`mvn --settings $SETTINGS test`
- 需要跳过测试的打包：`mvn --settings $SETTINGS -DskipTests package`
- 插件元数据检查：`mvn --settings $SETTINGS help:effective-pom` 或 `mvn --settings $SETTINGS help:describe -Dplugin=com.igaozp.dsd:mybatis-lint-maven-plugin`

## 代码风格与命名约定
- 目标 Java 8；默认 4 空格缩进，遵循标准 Maven 目录布局。
- 倾向小而聚焦的类与方法；插件 goal 尽量线程安全、无状态。
- 插件相关类按职责命名（如 `*Mojo`、`*Analyzer`）；测试以类名 + `Test` 后缀对应。

## 测试指南
- 遵循 `docs/rules/tdd.md` 的 TDD 循环：协作时明确 Red → Green → Refactor 阶段。
- 框架：JUnit 5 (Jupiter) 与 Mockito。测试放在 `src/test/java`，按功能或组件分组。
- 编写覆盖重复 SQL 场景的期望驱动测试（XML 重复、注解/XML 冲突、方法重载冲突、Wrapper 使用等），保持快速且隔离。
- 每次重要变更后运行 `mvn --settings $SETTINGS test`；重构前确保构建保持绿色。

## 提交与 PR 指南
- 提交信息采用简短祈使句（如 “Add duplicate SQL scanner skeleton”），必要时注明作用范围。
- PR 描述变更，关联任务/Issue，列出执行的测试命令，并标明构建标志或排除项。
- 规则变更或检测行为调整时，附上前后对比与示例 SQL/Mapper 片段说明。

## 安全与配置提示
- 不要提交内部 `settings.xml` 或凭据；本地引用 `--settings` 即可。
- 示例 SQL 与 Mapper XML 视为可能敏感；在基准用例或文档中先做脱敏处理。
