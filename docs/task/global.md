# 总体任务目标

## 项目背景与目标

项目对基于 Java 的多模块 Maven 项目，使用 Mybatis、MyBatis-Plus 进行数据访问。我们需要开发一个在编译期执行的 Maven 插件，用于检测 DAO 层是否存在重复 SQL：也就是不同 DAO 方法具有相同的方法签名且对应的 SQL 查询内容重复的情况。

这种“重复SQL”现象通常表现为以下几种形式：

1. XML内部重复：在同一个XML Mapper文件中，或者映射到同一Namespace的多个XML文件中，存在两个id相同的<select>、<update>等标签。

2. 注解与XML冲突：Java接口方法既使用了@Select、@Update、@Insert、@Delete 等注解，又在对应的XML文件中定义了同名的Statement ID。

3. 方法重载引发的隐式冲突：Java层存在重载方法（如query(int id)与query(String name)），但XML层仅定义了一个id="query"的Statement，导致MyBatis在运行时无法精准绑定或绑定错误。

4. MyBatis-Plus 条件构造器：使用 MyBatis-Plus 提供的 QueryWrapper、LambdaQueryWrapper、UpdateWrapper 等封装查询条件的形式。这些 Wrapper 通过链式调用设置查询条件。

如果发现重复 SQL，该插件能够在构建阶段自动检测这类问题并中断构建，从而实现编译时检查、尽早失败的最佳实践。

插件需兼容 Maven 多模块项目，能够在整个项目范围内检测重复 SQL。

## 规则要求

请按照 docs/rules/tdd.md 文件中的规则进行开发。