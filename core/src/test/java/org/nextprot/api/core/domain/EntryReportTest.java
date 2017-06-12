package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class EntryReportTest {

    @Test
    public void shouldSortAccordingToPositionUnknownsLast() throws Exception {

        Comparator<EntryReport> comparator = EntryReport.newByChromosomalPositionComparator();

        List<EntryReport> list = Arrays.asList(
            newEntryReport("SASS6", "NX_Q6UVJ0", "1p21.2",
                    "100083563", "100132955", ProteinExistenceLevel.PROTEIN_LEVEL,
                    false, true, false, false, 1, 246, 6,
                    "Spindle assembly abnormal protein 6 homolog"),
            newEntryReport("ISG15", "NX_P05161", "1p36.33",
                    "1001138", "1014541", ProteinExistenceLevel.PROTEIN_LEVEL,
                    false, true, false, false, 1, 101, 6,
                    "Ubiquitin-like protein ISG15"),
            newEntryReport("KDM1A", "NX_O60341", "1p36.12",
                    "23019448", "23083689", ProteinExistenceLevel.PROTEIN_LEVEL,
                    false, true, false, false, 2, 187, 33,
                    "Lysine-specific histone demethylase 1A"),
            newEntryReport("NBPF26", "NX_B4DH59", "1q21.1",
                    "-", "-", ProteinExistenceLevel.UNCERTAIN,
                    false, true, false, false, 1, 0, 0,
                    "Neuroblastoma breakpoint family member 26"),
            newEntryReport("PGBD5", "NX_Q8N414", "1q42.13",
                    "230314482", "230426371", ProteinExistenceLevel.PROTEIN_LEVEL,
                    false, true, false, false, 1, 236, 2,
                    "PiggyBac transposable element-derived protein 5")
        );

        list.sort(comparator);

        Assert.assertEquals("NX_P05161", list.get(0).getAccession());
        Assert.assertEquals("NX_O60341", list.get(1).getAccession());
        Assert.assertEquals("NX_Q6UVJ0", list.get(2).getAccession());
        Assert.assertEquals("NX_Q8N414", list.get(3).getAccession());
        Assert.assertEquals("NX_B4DH59", list.get(4).getAccession());
    }

    @Test
    public void shouldSortAccordingToPositionUnknownsBandLast() throws Exception {

        Comparator<EntryReport> comparator = EntryReport.newByChromosomalPositionComparator();

        List<EntryReport> list = Arrays.asList(
            newEntryReport("HNRNPCL1", "NX_O60812", "1",
                    "12847408", "12848725", ProteinExistenceLevel.PROTEIN_LEVEL,
                    true, false, false, false, 1, 322, 5,
                    "Heterogeneous nuclear ribonucleoprotein C-like 1"),
            newEntryReport("HNRNPCL1", "NX_O60812", "1p36.21",
                    "12847408", "12848725", ProteinExistenceLevel.PROTEIN_LEVEL,
                    true, false, false, false, 1, 322, 5,
                    "Heterogeneous nuclear ribonucleoprotein C-like 1")
        );

        list.sort(comparator);

        Assert.assertEquals("1p36.21", list.get(0).getChromosomalLocation());
        Assert.assertEquals("1", list.get(1).getChromosomalLocation());
    }

    @Test
    public void shouldSortAccordingToPositionOfBands() throws Exception {

        Comparator<EntryReport> comparator = EntryReport.newByChromosomalPositionComparator();

        List<EntryReport> list = Arrays.asList(
                newEntryReport("HNRNPCL1", "NX_O60812", "1q32",
                        "12847408", "12848725", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1p21.1",
                        "12847408", "12848725", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1q13.4",
                        "12847408", "12848725", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1p1.2-p1.4",
                        "12847408", "12848725", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1p36.21",
                        "12847408", "12848725", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1")
        );

        list.sort(comparator);

        Assert.assertEquals("1p36.21", list.get(0).getChromosomalLocation());
        Assert.assertEquals("1p21.1", list.get(1).getChromosomalLocation());
        Assert.assertEquals("1p1.2-p1.4", list.get(2).getChromosomalLocation());
        Assert.assertEquals("1q13.4", list.get(3).getChromosomalLocation());
        Assert.assertEquals("1q32", list.get(4).getChromosomalLocation());
    }

    @Test
    public void shouldSortAccordingToAccession() throws Exception {

        Comparator<EntryReport> comparator = EntryReport.newByChromosomalPositionComparator();

        List<EntryReport> list = Arrays.asList(
                newEntryReport("KIAA0754", "NX_O94854", "1p34.2",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ERVK11-1", "NX_P61568", "1p13.3",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ANKRD20A12P", "NX_Q8NF67", "1q12",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("NBPF26", "NX_B4DH59", "1q21.1",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("NBPF20", "NX_Q3BBV1", "1q21.1",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("NBPF9", "NX_Q3BBW0", "1q21.1",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("NBPF8", "NX_Q3BBV2", "1q21.11",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ERVK-7", "NX_P61567", "1q22",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ERVK-7", "NX_P61582", "1q22",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ERVK-7", "NX_P63130", "1q22",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ERVK-7", "NX_P63131", "1q22",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ERVK-7", "NX_P63135", "1q22",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("ERVK-18", "NX_O42043", "1q23.3",
                        "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1")
        );

        list.sort(comparator);

        List<String> expectedAccessions = Arrays.asList("NX_B4DH59", "NX_O42043", "NX_O94854", "NX_P61567", "NX_P61568",
                "NX_P61582", "NX_P63130", "NX_P63131", "NX_P63135", "NX_Q3BBV1", "NX_Q3BBV2", "NX_Q3BBW0", "NX_Q8NF67");

        for (int i=0 ; i<expectedAccessions.size() ; i++) {
            Assert.assertEquals(expectedAccessions.get(i), list.get(i).getAccession());
        }
    }

    public static EntryReport newEntryReport(String geneName, String ac, String chromosalPosition,
                                             String startPos, String stopPos, ProteinExistenceLevel protExistence,
                                             boolean isProteomics, boolean  isAntibody, boolean  is3D, boolean  isDisease,
                                             int isoformCount, int  variantCount, int  ptmCount, String  description) throws ParseException {

        EntryReport entryReport = new EntryReport();

        ChromosomalLocation cl = ChromosomalLocation.fromString(chromosalPosition);
        cl.setRecommendedName(geneName);
        cl.setFirstPosition((startPos.equals("-"))?0:Integer.parseInt(startPos));
        cl.setLastPosition((stopPos.equals("-"))?0:Integer.parseInt(stopPos));

        entryReport.setAccession(ac);
        entryReport.setChromosomalLocation(cl);
        entryReport.setProteinExistence(protExistence);
        entryReport.setPropertyTest(EntryReport.IS_PROTEOMICS, isProteomics);
        entryReport.setPropertyTest(EntryReport.IS_ANTIBODY, isAntibody);
        entryReport.setPropertyTest(EntryReport.IS_3D, is3D);
        entryReport.setPropertyTest(EntryReport.IS_DISEASE, isDisease);
        entryReport.setPropertyCount(EntryReport.ISOFORM_COUNT, isoformCount);
        entryReport.setPropertyCount(EntryReport.VARIANT_COUNT, variantCount);
        entryReport.setPropertyCount(EntryReport.PTM_COUNT, ptmCount);
        entryReport.setDescription(description);

        return entryReport;
    }
}