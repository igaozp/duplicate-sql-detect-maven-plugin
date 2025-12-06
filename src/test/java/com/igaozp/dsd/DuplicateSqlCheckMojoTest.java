package com.igaozp.dsd;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DuplicateSqlCheckMojoTest {

    @Mock
    private Log log;

    @Test
    void testSkip() throws Exception {
        DuplicateSqlCheckMojo mojo = new DuplicateSqlCheckMojo();
        setField(mojo, "skip", true);
        mojo.setLog(log);

        mojo.execute();

        verify(log).info("SQL 重复检测已跳过");
    }

    @Test
    void testNoDuplicates() throws Exception {
        DuplicateSqlCheckMojo mojo = spy(new DuplicateSqlCheckMojo());
        setField(mojo, "skip", false);
        setField(mojo, "failOnDuplicate", true);
        doReturn(new DuplicateReport(0)).when(mojo).runDetection();

        assertDoesNotThrow(mojo::execute);
    }

    @Test
    void testDuplicatesFail() throws Exception {
        DuplicateSqlCheckMojo mojo = spy(new DuplicateSqlCheckMojo());
        setField(mojo, "skip", false);
        setField(mojo, "failOnDuplicate", true);
        doReturn(new DuplicateReport(1)).when(mojo).runDetection();

        assertThrows(MojoFailureException.class, mojo::execute);
    }

    @Test
    void testDuplicatesNoFail() throws Exception {
        DuplicateSqlCheckMojo mojo = spy(new DuplicateSqlCheckMojo());
        setField(mojo, "skip", false);
        setField(mojo, "failOnDuplicate", false);
        doReturn(new DuplicateReport(1)).when(mojo).runDetection();

        assertDoesNotThrow(mojo::execute);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = getFieldFromHierarchy(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Field getFieldFromHierarchy(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
