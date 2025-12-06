package com.igaozp.dsd;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmlMapperParser {

    private static final Set<String> SQL_ELEMENTS = new HashSet<>(
            Arrays.asList("select", "insert", "update", "delete"));

    public MapperInfo parseMapperXml(File xmlFile) throws Exception {
        SAXReader reader = new SAXReader();
        // Disable DTD validation to avoid network requests
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document document = reader.read(xmlFile);
        Element root = document.getRootElement();

        String namespace = root.attributeValue("namespace");
        MapperInfo mapperInfo = new MapperInfo(namespace, xmlFile.getPath());

        // Parse all SQL statements
        for (Element element : root.elements()) {
            if (!SQL_ELEMENTS.contains(element.getName())) {
                continue;
            }
            SqlStatement stmt = new SqlStatement();
            stmt.setId(element.attributeValue("id"));
            stmt.setType(element.getName());
            stmt.setFullId(namespace + "." + stmt.getId());
            stmt.setLineNumber(0);
            stmt.setSource(DefinitionSource.XML);

            mapperInfo.addStatement(stmt);
        }

        return mapperInfo;
    }

    /**
     * Scan all Mapper XML files in the directory
     */
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
            String content = new String(Files.readAllBytes(path));
            return content.contains("mybatis.org/dtd/mybatis-3-mapper.dtd")
                    || content.contains("<mapper namespace=");
        } catch (IOException e) {
            return false;
        }
    }
}
