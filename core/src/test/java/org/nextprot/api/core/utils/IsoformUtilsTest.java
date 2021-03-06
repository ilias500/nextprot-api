package org.nextprot.api.core.utils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ActiveProfiles({ "dev" })
public class IsoformUtilsTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Test
    public void testNumericSortIsoNamesIfContainDash() throws Exception {

        List<String> list = Arrays.asList("NX_Q13557-1", "NX_Q13557-10", "NX_Q13557-12", "NX_Q13557-3", "NX_Q13557-9", "NX_Q13557-8");

        Collections.sort(list, new IsoformUtils.ByIsoformUniqueNameComparator());

        Assert.assertEquals(Arrays.asList("NX_Q13557-1", "NX_Q13557-3", "NX_Q13557-8", "NX_Q13557-9", "NX_Q13557-10", "NX_Q13557-12"), list);
    }

    @Test
    public void testLexicalSortIsoNamesIfNoDash() throws Exception {

        List<String> list = Arrays.asList("NX_Q13557_1", "NX_Q13557_10", "NX_Q13557_12", "NX_Q13557_3", "NX_Q13557_9", "NX_Q13557_8");

        Collections.sort(list, new IsoformUtils.ByIsoformUniqueNameComparator());

        Assert.assertEquals(Arrays.asList("NX_Q13557_1", "NX_Q13557_10", "NX_Q13557_12", "NX_Q13557_3", "NX_Q13557_8", "NX_Q13557_9"), list);
    }

    @Test
    public void testSort() throws Exception {

        List<String> list = Arrays.asList("NX_Q13557-1", "NX_Q13557-10", "NX_Q13557-12", "NX_Q13557-3", "NX_Q13557-9", "NX_Q13557-8", "NX_P01306-1", "NX_P01306-2");

        Collections.sort(list, new IsoformUtils.ByIsoformUniqueNameComparator());

        Assert.assertEquals(Arrays.asList("NX_P01306-1", "NX_P01306-2", "NX_Q13557-1", "NX_Q13557-3", "NX_Q13557-8", "NX_Q13557-9", "NX_Q13557-10", "NX_Q13557-12"), list);
    }

    @Test
    public void testSortIsoform() throws Exception {

        List<Isoform> list = createIsoforms("2AB", "2AC", "2BC", "2A", "2B", "2C", "Iso 2", "1ABC", "Iso 12",
                "3ABC", "3AB", "3AC", "1AB", "3BC", "3A", "3B", "3C", "Iso 3", "4ABC", "4AB", "4AC", "4BC",
                "4A", "1AC", "4B", "4C", "Iso 4", "1BC", "1A", "1B", "1C", "Iso 1", "2ABC", "11AC", "ABC34 iso");

        list.get(7).setSwissProtDisplayedIsoform(true);

        Collections.sort(list, new IsoformUtils.IsoformComparator());

        Assert.assertEquals(Arrays.asList(
                "1ABC",  // canonical comes first
                "1A", "1AB", "1AC", "1B", "1BC", "1C",
                "2A", "2AB", "2ABC", "2AC", "2B", "2BC", "2C",
                "3A", "3AB", "3ABC", "3AC", "3B", "3BC", "3C",
                "4A", "4AB", "4ABC", "4AC", "4B", "4BC", "4C",
                "11AC", "ABC34 iso", "Iso 1", "Iso 2", "Iso 3", "Iso 4", "Iso 12"), new ArrayList<>(Collections2.transform(list, new Function<Isoform, String>() {

            @Nullable
            @Override
            public String apply(Isoform iso) {

                return iso.getMainEntityName().getValue();
            }
        })));
    }

    @Test
    public void testGetIsoformByAccession() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Assert.assertEquals("NX_P01308-1", IsoformUtils.getIsoformByName(entry, "NX_P01308-1").getUniqueName());
    }

    @Test
    public void testGetIsoformByName() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P06213").withTargetIsoforms());

        Assert.assertEquals("NX_P06213-1", IsoformUtils.getIsoformByName(entry, "Long").getUniqueName());
    }

    @Test
    public void testGetOtherIsoforms() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Isoform isoform = entry.getIsoforms().get(0);

        Assert.assertTrue(IsoformUtils.getOtherIsoforms(entry, isoform.getUniqueName()).isEmpty());
    }

    @Test
    public void testGetOtherIsoforms2() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_Q9UI33").withTargetIsoforms());

        List<Isoform> others = IsoformUtils.getOtherIsoforms(entry, "NX_Q9UI33-1");
        Assert.assertEquals(2, others.size());
        for (Isoform isoform : others) {
            Assert.assertTrue(
                    isoform.getUniqueName().equals("NX_Q9UI33-2") ||
                            isoform.getUniqueName().equals("NX_Q9UI33-3") );
        }
    }

    @Test
    public void testGetCanonicalIsoform() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Assert.assertNotNull(IsoformUtils.getCanonicalIsoform(entry));
        Assert.assertEquals("NX_P01308-1", IsoformUtils.getCanonicalIsoform(entry).getUniqueName());
    }

    @Test
    public void testGetIsoformByNameLowerCase() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P06213").withTargetIsoforms());

        Assert.assertEquals("NX_P06213-1", IsoformUtils.getIsoformByName(entry, "long").getUniqueName());
    }

    private List<Isoform> createIsoforms(String... mainNames) {

        List<Isoform> list = new ArrayList<>();

        for (String mainName : mainNames) {

            Isoform isoform = new Isoform();

            EntityName name = new EntityName();

            name.setName(mainName);

            isoform.setMainEntityName(name);

            list.add(isoform);
        }

        return list;
    }
}