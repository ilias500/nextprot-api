package org.nextprot.api.user.utils;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.commons.exception.EntrySetNotFoundException;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.nextprot.api.user.service.UserProteinListServiceTest.createUserProteinList;

public class UserProteinListUtilsTest {

    @Test(expected = NextProtException.class)
    public void testCombineSameListThrowsException() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinListUtils.combine(l1, l1, UserProteinListService.Operator.OR, "bobleponge", "coolio", null);
    }

    @Test
    public void testCombineOr() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P123", "NX_P321"));

        UserProteinList l3 = UserProteinListUtils.combine(l1, l2, UserProteinListService.Operator.OR, "bobleponge", "coolio", null);

        assertEquals("coolio", l3.getName());
        assertEquals(3, l3.getAccessionNumbers().size());
        assertEquals("bobleponge", l3.getOwner());
        assertEquals(Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"), l3.getAccessionNumbers());
    }

    @Test
    public void testCombineOr2() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P123", "NX_P321"));

        UserProteinList l3 = UserProteinListUtils.combine(l2, l1, UserProteinListService.Operator.OR, "bobleponge", "coolio", null);

        assertEquals("coolio", l3.getName());
        assertEquals(3, l3.getAccessionNumbers().size());
        assertEquals("bobleponge", l3.getOwner());
        assertEquals(Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"), l3.getAccessionNumbers());
    }

    @Test
    public void testCombineAnd() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P123", "NX_P321"));

        UserProteinList l3 = UserProteinListUtils.combine(l1, l2, UserProteinListService.Operator.AND, "bobleponge", "homie", null);

        assertEquals("homie", l3.getName());
        assertEquals(1, l3.getAccessionNumbers().size());
        assertEquals("bobleponge", l3.getOwner());
        assertEquals("NX_P123", l3.getAccessionNumbers().iterator().next());
    }

    @Test
    public void testCombineAnd2() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P123", "NX_P321"));

        UserProteinList l3 = UserProteinListUtils.combine(l2, l1, UserProteinListService.Operator.AND, "bobleponge", "homie", null);

        assertEquals("homie", l3.getName());
        assertEquals(1, l3.getAccessionNumbers().size());
        assertEquals("bobleponge", l3.getOwner());
        assertEquals("NX_P123", l3.getAccessionNumbers().iterator().next());
    }

    @Test(expected = NextProtException.class)
    public void testCombineAndEmptySetThrowsException() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P124", "NX_P455"));

        UserProteinListUtils.combine(l1, l2, UserProteinListService.Operator.AND, "bobleponge", "coolio", null);
    }

    @Test
    public void testCombineNotIn() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P123", "NX_P321"));

        UserProteinList l3 = UserProteinListUtils.combine(l1, l2, UserProteinListService.Operator.NOT_IN, "bobleponge", "rap", null);

        assertEquals("rap", l3.getName());
        assertEquals(1, l3.getAccessionNumbers().size());
        assertEquals("bobleponge", l3.getOwner());
        assertEquals("NX_P456", l3.getAccessionNumbers().iterator().next());
    }

    @Test
    public void testCombineNotIn2() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P123", "NX_P321"));

        UserProteinList l3 = UserProteinListUtils.combine(l2, l1, UserProteinListService.Operator.NOT_IN, "bobleponge", "rap", null);

        assertEquals("rap", l3.getName());
        assertEquals(1, l3.getAccessionNumbers().size());
        assertEquals("bobleponge", l3.getOwner());
        assertEquals("NX_P321", l3.getAccessionNumbers().iterator().next());
    }

    @Test(expected = NextProtException.class)
    public void testCombineNotInEmptySetThrowsException() {

        UserProteinList l1 = createUserProteinList("cool1", Sets.newHashSet("NX_P123", "NX_P456"));
        UserProteinList l2 = createUserProteinList("cool2", Sets.newHashSet("NX_P123", "NX_P456"));

        UserProteinListUtils.combine(l1, l2, UserProteinListService.Operator.NOT_IN, "bobleponge", "coolio", null);
    }

    @Test
    public void testParseCommentedAccessionNumbers() throws IOException {

        StringReader reader = new StringReader("#NX_P123\n# NX_P456\n#NX_P321");

        Set<String> set = UserProteinListUtils.parseAccessionNumbers(reader, Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"));

        assertTrue(set.isEmpty());
    }

    @Test(expected = EntrySetNotFoundException.class)
    public void testParseAccessionNumbers() throws IOException {

        StringReader reader = new StringReader("NX_P123\nNX_P456\nNX_P321");

        Set<String> set = UserProteinListUtils.parseAccessionNumbers(reader, Sets.newHashSet("NX_P123", "NX_P321"));

        assertEquals(Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"), set);
    }

    @Test
    public void testParseAccessionNumbersIgnoreNotFoundException() throws IOException {

        StringReader reader = new StringReader("NX_P123\nNX_P456\nNX_P321");

        Set<String> set = UserProteinListUtils.parseAccessionNumbers(reader, Sets.newHashSet("NX_P123", "NX_P321"), true);

        assertEquals(Sets.newHashSet("NX_P123", "NX_P321"), set);
    }

    @Test
    public void testParseUniprotNumbers() throws IOException {

        StringReader reader = new StringReader("P123\nP456\nNX_P321");

        Set<String> set = UserProteinListUtils.parseAccessionNumbers(reader, Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"));

        assertEquals(Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"), set);
    }

    @Test
    public void testCheckAndFormatAccessionNumber() throws IOException {

        Set<String> collector = new HashSet<>();

        UserProteinListUtils.checkFormatAndCollectValidAccessionNumber("P123", collector, Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"));

        assertEquals(1, collector.size());
        assertTrue(collector.contains("NX_P123"));
    }

    @Test
    public void testCheckAndFormatAccessionNumbersWithCommentedElement() throws IOException {

        Set<String> validAccessions = UserProteinListUtils.checkAndFormatAccessionNumbers(Arrays.asList("P123", "P456", "NX_P321", "#dewfref"),
                Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"));

        assertEquals(Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"), validAccessions);
    }

    @Test(expected = EntrySetNotFoundException.class)
    public void testCheckAndFormatAccessionNumbers2() throws IOException {

        Set<String> validAccessions = UserProteinListUtils.checkAndFormatAccessionNumbers(Arrays.asList("P123", "P456", "NX_P321", "dewfref"),
                Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"));

        assertEquals(Sets.newHashSet("NX_P123", "NX_P456", "NX_P321"), validAccessions);
    }
}
