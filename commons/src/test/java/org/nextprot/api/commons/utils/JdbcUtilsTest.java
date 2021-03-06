package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.exception.NextProtException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.KeyHolder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class JdbcUtilsTest {

    @Test
    public void test() throws Exception {

        Number generatedKey = JdbcUtils.insertAndGetKey("insert whatever", "column name", mockKeyHolder(123), Mockito.mock(MapSqlParameterSource.class), mockOperation());

        Assert.assertEquals(123, generatedKey);
    }

    // TODO: we should also check the correctness of the exception message
    @Test(expected = NextProtException.class)
    public void testNonExpectedEmptySql() throws Exception {

        JdbcUtils.insertAndGetKey("", "column name", mockKeyHolder(123), Mockito.mock(MapSqlParameterSource.class), mockOperation());
    }

    @Test(expected = NextProtException.class)
    public void testNonExpectedEmptyColumnName() throws Exception {

        JdbcUtils.insertAndGetKey("bla", "", mockKeyHolder(123), Mockito.mock(MapSqlParameterSource.class), mockOperation());
    }

    private NamedParameterJdbcOperations mockOperation() {

        NamedParameterJdbcOperations operations = Mockito.mock(NamedParameterJdbcOperations.class);
        when(operations.update(anyString(), any(MapSqlParameterSource.class), any(KeyHolder.class), any(String[].class))).thenReturn(1);

        return operations;
    }

    private KeyHolder mockKeyHolder(int expectedGeneratedNumber) {

        KeyHolder keyHolder = Mockito.mock(KeyHolder.class);
        when(keyHolder.getKey()).thenReturn(expectedGeneratedNumber);

        return keyHolder;
    }
}