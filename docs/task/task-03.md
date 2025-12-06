# Task 03
总体项目目标参考 task/global.md 文件

## 当前项目目标：

请根据当前文档描述完成本次任务:
1. 根据当前项目的代码实现 XML 解析器
2. 添加完成相关的单元测试

任务完成后请在当前文件后面添加任务的完成情况

### MyBatis XML Mapper 解析

MyBatis Mapper XML 的 `namespace` 属性必须与 Java Mapper 接口的全限定名完全匹配，这是建立 XML 与接口绑定关系的核心机制。
每个 `select`、`insert`、`update`、`delete` 元素的 `id` 属性对应接口中的方法名。

xml示例：
```xml
<mapper namespace="com.example.mapper.UserMapper">
    <sql id="baseColumns">id, username, email, created_at</sql>

    <select id="selectById" resultType="User">
        SELECT <include refid="baseColumns"/> FROM users WHERE id = #{id}
    </select>
</mapper>

```

推荐使用 **dom4j** 库进行 XML 解析，API 简洁且支持 XPath。以下是核心解析器实现参考：
```java
public class XmlMapperParser {

    private static final Set<String> SQL_ELEMENTS =
        Set.of("select", "insert", "update", "delete");

    public MapperInfo parseMapperXml(File xmlFile) throws Exception {
        SAXReader reader = new SAXReader();
        // 禁用 DTD 验证，避免网络请求影响性能
        reader.setFeature(
            "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document document = reader.read(xmlFile);
        Element root = document.getRootElement();

        String namespace = root.attributeValue("namespace");
        MapperInfo mapperInfo = new MapperInfo(namespace, xmlFile.getPath());

        // 解析 sql 片段用于后续 include 展开
        Map<String, String> sqlFragments = new HashMap<>();
        for (Element sqlElem : root.elements("sql")) {
            sqlFragments.put(sqlElem.attributeValue("id"),
                            sqlElem.getStringValue());
        }

        // 解析所有 SQL 语句
        for (Element element : root.elements()) {
            if (SQL_ELEMENTS.contains(element.getName())) {
                SqlStatement stmt = new SqlStatement();
                stmt.setId(element.attributeValue("id"));
                stmt.setType(element.getName());
                stmt.setFullId(namespace + "." + stmt.getId());
                stmt.setLineNumber(element.getTextPosition().getLineNumber());
                stmt.setSource(DefinitionSource.XML);

                mapperInfo.addStatement(stmt);
            }
        }

        return mapperInfo;
    }

    /** 扫描目录下所有 Mapper XML 文件 */
    public List<Path> scanMapperXmlFiles(String directory) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(directory))) {
            return stream
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".xml"))
                .filter(this::isMyBatisMapperXml)
                .collect(Collectors.toList());
        }
    }

    private boolean isMyBatisMapperXml(Path path) {
        try {
            String content = Files.readString(path);
            return content.contains("mybatis.org/dtd/mybatis-3-mapper.dtd")
                || content.contains("<mapper namespace=");
        } catch (IOException e) {
            return false;
        }
    }
}
```
## 任务完成情况

- [x] 根据当前项目的代码实现 XML 解析器
    - 已创建 DefinitionSource, SqlStatement, MapperInfo 数据模型
    - 已实现 XmlMapperParser 类，支持 dom4j 解析
- [x] 添加完成相关的单元测试
    - 已创建 src/test/resources/UserMapper.xml 测试资源
    - 已创建 XmlMapperParserTest 并通过所有测试
