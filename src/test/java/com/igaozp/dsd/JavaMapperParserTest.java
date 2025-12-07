package com.igaozp.dsd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class JavaMapperParserTest {

    @Test
    public void testParseSimpleMapper() throws FileNotFoundException {
        JavaMapperParser parser = new JavaMapperParser();
        File file = new File("src/test/resources/java-mapper/SimpleMapper.java");
        MapperInfo info = parser.parseMapperInterface(file);

        Assertions.assertEquals("com.igaozp.dsd.test.SimpleMapper", info.getNamespace());
        Assertions.assertFalse(info.isExtendsBaseMapper());

        List<SqlStatement> statements = info.getStatements();
        Assertions.assertEquals(2, statements.size());

        SqlStatement select = statements.stream().filter(s -> s.getId().equals("selectById")).findFirst().orElse(null);
        Assertions.assertNotNull(select);
        Assertions.assertEquals("select", select.getType());
        Assertions.assertEquals("SELECT * FROM users WHERE id = #{id}", select.getRawSql());
    }

    @Test
    public void testParsePlusMapper() throws FileNotFoundException {
        JavaMapperParser parser = new JavaMapperParser();
        File file = new File("src/test/resources/java-mapper/PlusMapper.java");
        MapperInfo info = parser.parseMapperInterface(file);

        Assertions.assertEquals("com.igaozp.dsd.test.PlusMapper", info.getNamespace());
        Assertions.assertTrue(info.isExtendsBaseMapper());

        List<SqlStatement> statements = info.getStatements();
        Assertions.assertEquals(1, statements.size());
        Assertions.assertEquals("customMethod", statements.get(0).getId());
    }

    @Test
    public void testBaseMapperMethods() {
        JavaMapperParser parser = new JavaMapperParser();
        Assertions.assertTrue(parser.isBaseMapperMethod("selectById"));
        Assertions.assertTrue(parser.isBaseMapperMethod("insert"));
        Assertions.assertFalse(parser.isBaseMapperMethod("customMethod"));
    }
}
