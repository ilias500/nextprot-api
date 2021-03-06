package org.nextprot.api.blast.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.commons.exception.NextProtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class BlastDbMakerTest {

    @Value("${makeblastdb.bin}")
    private String makeblastdbBinPath;

    private BlastProgram.Params params;

    @Test
    public void testCommandLineBuilding() throws Exception {

        params = new BlastProgram.Params(makeblastdbBinPath, "/tmp/blastdb");

        BlastDbMaker runner = new BlastDbMaker(params);

        File file = new File("/tmp/input.fasta");
        List<String> cl = runner.buildCommandLine(params, file);

        Assert.assertEquals(Arrays.asList(makeblastdbBinPath, "-dbtype", "prot", "-title",
                "nextprot", "-in", "/tmp/input.fasta", "-out", "/tmp/blastdb"), cl);
    }

    @Test(expected = NextProtException.class)
    public void shouldNotBeAbleToCreateInstance() throws Exception {

        params = new BlastProgram.Params(null, "/tmp/blastdb");

        new BlastDbMaker(params);
    }

    //@Test
    public void shouldCreateDbFromIsoformSequence() throws Exception {

        params = new BlastProgram.Params(makeblastdbBinPath, "/tmp/blastdb");

        BlastDbMaker runner = new BlastDbMaker(params);

        Map<String, String> sequences = new HashMap<>();
        sequences.put("NX_P01308-1", "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRREAEDLQVGQVELGGGPGAGSLQPLALEGSLQKRGIVEQCCTSICSLYQLENYCN");

        String result = runner.run(sequences);

        Assert.assertTrue(result.contains("Building a new DB, current time:"));
        Assert.assertTrue(result.contains("New DB name:   /tmp/blastdb"));
        Assert.assertTrue(result.contains("New DB title:  nextprot"));
        Assert.assertTrue(result.contains("Sequence type: Protein"));
        Assert.assertTrue(result.contains("Adding sequences from FASTA; added 1 sequences"));
    }

    @Test(expected = NextProtException.class)
    public void shouldNotCreateDbFromSequenceWithBadlyFormattedIsoAccession() throws Exception {

        params = new BlastProgram.Params(makeblastdbBinPath, "/tmp/blastdb");

        BlastDbMaker runner = new BlastDbMaker(params);

        Map<String, String> sequences = new HashMap<>();
        sequences.put("NX_P01308", "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRREAEDLQVGQVELGGGPGAGSLQPLALEGSLQKRGIVEQCCTSICSLYQLENYCN");

        runner.run(sequences);
    }
}