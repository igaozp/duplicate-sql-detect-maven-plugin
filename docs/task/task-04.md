# Task 03
总体项目目标参考 task/global.md 文件

## 当前项目目标：MyBatis 注解方式 SQL 解析

请根据当前文档描述完成本次任务:
1. 根据当前项目的代码实现代码中注解解析器
2. 添加完成相关的单元测试

任务完成后请在当前文件后面添加任务的完成情况

### 注解类型概览

MyBatis 支持两类 SQL 注解：**静态 SQL 注解**（`@Select`、`@Insert`、`@Update`、`@Delete`）直接包含 SQL 语句；**Provider 注解**（`@SelectProvider` 等）指向动态生成 SQL 的类和方法。

```java
// 静态 SQL 注解
@Select("SELECT * FROM users WHERE id = #{id}")
User selectById(Long id);

// Provider 动态 SQL
@SelectProvider(type = UserSqlProvider.class, method = "buildSelectSql")
List<User> selectByCondition(Map<String, Object> params);

```

### 使用 JavaParser 解析源码

由于 Maven 插件通常在编译阶段之前执行，class 文件可能不存在，因此推荐使用 **JavaParser** 直接解析 Java 源文件：

```java
public class JavaMapperParser {

    public MapperInfo parseMapperInterface(File javaFile) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(javaFile);

        MapperInfo mapperInfo = new MapperInfo();

        cu.findAll(ClassOrInterfaceDeclaration.class).stream()
          .filter(ClassOrInterfaceDeclaration::isInterface)
          .forEach(interfaceDecl -> {
              String packageName = cu.getPackageDeclaration()
                  .map(pd -> pd.getNameAsString()).orElse("");
              String fullName = packageName.isEmpty()
                  ? interfaceDecl.getNameAsString()
                  : packageName + "." + interfaceDecl.getNameAsString();

              mapperInfo.setNamespace(fullName);
              mapperInfo.setFilePath(javaFile.getPath());

              // 检查是否继承 BaseMapper（MyBatis-Plus）
              boolean extendsBaseMapper = interfaceDecl.getExtendedTypes().stream()
                  .anyMatch(t -> t.getNameAsString().contains("BaseMapper"));
              mapperInfo.setExtendsBaseMapper(extendsBaseMapper);

              // 解析所有方法上的 SQL 注解
              interfaceDecl.getMethods().forEach(method -> {
                  parseMethodAnnotations(method, mapperInfo);
              });
          });

        return mapperInfo;
    }

    private void parseMethodAnnotations(MethodDeclaration method, MapperInfo mapperInfo) {
        Set<String> sqlAnnotations = Set.of("Select", "Insert", "Update", "Delete");
        Set<String> providerAnnotations = Set.of(
            "SelectProvider", "InsertProvider", "UpdateProvider", "DeleteProvider");

        method.getAnnotations().forEach(annotation -> {
            String annName = annotation.getNameAsString();

            if (sqlAnnotations.contains(annName) || providerAnnotations.contains(annName)) {
                SqlStatement stmt = new SqlStatement();
                stmt.setId(method.getNameAsString());
                stmt.setType(annName.replace("Provider", "").toLowerCase());
                stmt.setFullId(mapperInfo.getNamespace() + "." + stmt.getId());
                stmt.setSource(DefinitionSource.ANNOTATION);
                stmt.setLineNumber(method.getBegin()
                    .map(p -> p.line).orElse(-1));

                // 提取注解中的 SQL 内容（用于高级分析）
                if (annotation instanceof SingleMemberAnnotationExpr) {
                    String sql = ((SingleMemberAnnotationExpr) annotation)
                        .getMemberValue().toString();
                    stmt.setRawSql(sql);
                }

                mapperInfo.addStatement(stmt);
            }
        });
    }
}

```

### MyBatis-Plus 特殊处理

对于继承 `BaseMapper` 的接口，需要注意 **18 个内置方法**（如 `selectById`、`insert`、`updateById` 等）是由框架自动生成的，不应被视为用户定义的重复：
```java
private static final Set<String> BASE_MAPPER_METHODS = Set.of(
    "insert", "deleteById", "deleteBatchIds", "deleteByMap", "delete",
    "updateById", "update", "selectById", "selectBatchIds", "selectByMap",
    "selectOne", "selectList", "selectMaps", "selectCount", "selectPage",
    "selectMapsPage", "selectObjs", "exists"
);

public boolean isBaseMapperMethod(String methodName) {
    return BASE_MAPPER_METHODS.contains(methodName);
}

```

## 任务完成情况

- [x] 实现注解解析器 `JavaMapperParser`
- [x] 添加单元测试 `JavaMapperParserTest`
- [x] 验证通过 (单元测试 `mvn test` 通过)
