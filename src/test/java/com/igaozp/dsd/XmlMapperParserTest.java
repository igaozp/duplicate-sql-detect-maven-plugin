package com.igaozp.dsd;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlMapperParserTest {

    @Test
    void testParseMapperXml() throws Exception {
        XmlMapperParser parser = new XmlMapperParser();
        File file = new File("src/test/resources/UserMapper.xml");

        // Ensure file exists before testing
        assertTrue(file.exists(), "Test resource file should exist");

        MapperInfo mapperInfo = parser.parseMapperXml(file);

        assertNotNull(mapperInfo);
        assertEquals("com.example.mapper.UserMapper", mapperInfo.getNamespace());
        assertEquals(4, mapperInfo.getStatements().size());

        SqlStatement selectStmt = mapperInfo.getStatements().stream()
                .filter(s -> "selectById".equals(s.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(selectStmt);
        assertEquals("select", selectStmt.getType());
        assertEquals("com.example.mapper.UserMapper.selectById", selectStmt.getFullId());
    }

    @Test
    void testScanMapperXmlFiles() throws Exception {
        XmlMapperParser parser = new XmlMapperParser();
        List<Path> files = parser.scanMapperXmlFiles("src/test/resources");

        assertFalse(files.isEmpty(), "Should find at least one XML file");
        assertTrue(files.stream().anyMatch(p -> p.endsWith("UserMapper.xml")));
    }
}
