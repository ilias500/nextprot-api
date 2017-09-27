package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

public class NullableComparatorTest {

    private final NullableComparator<StringInteger> nullableComparator = new NullableComparator<>(Comparator.comparingInt(StringInteger::getInteger));

    @Test
    public void testComparePositionAsc() throws Exception {

        int cmp = nullableComparator.compareNullables(new StringInteger("2"), new StringInteger("25"), true);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionDesc() throws Exception {

        int cmp = nullableComparator.compareNullables(new StringInteger("2"), new StringInteger("25"), false);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionWithBeginNull() throws Exception {

        int cmp = nullableComparator.compareNullables(null, new StringInteger("25"));

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionWithBeginNull2() throws Exception {

        int cmp = nullableComparator.compareNullables(new StringInteger("19"), null);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionBothNull() throws Exception {

        int cmp = nullableComparator.compareNullables(null, null);

        Assert.assertTrue(cmp == 0);
    }

    private class StringInteger {

        private final int i;

        public StringInteger(String str) {

            this.i = Integer.parseInt(str);
        }

        public int getInteger() {
            return i;
        }
    }
}