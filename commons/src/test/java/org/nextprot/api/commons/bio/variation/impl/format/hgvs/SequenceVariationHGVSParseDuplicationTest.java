package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

import java.text.ParseException;

public class SequenceVariationHGVSParseDuplicationTest {

    SequenceVariantHGVSFormat format = new SequenceVariantHGVSFormat();

    @Test
    public void testParseSimpleDuplication() throws ParseException {

        SequenceVariation duplication = format.parse("p.Val417dup");

        Assert.assertEquals(AminoAcidCode.VALINE, duplication.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.VALINE, duplication.getLastChangingAminoAcid());
        Assert.assertEquals(417, duplication.getFirstChangingAminoAcidPos());
        Assert.assertEquals(417, duplication.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DUPLICATION, duplication.getSequenceChange().getType());
        Assert.assertEquals(417, duplication.getSequenceChange().getValue());
    }

    @Test
    public void testParseDuplication() throws Exception {

        SequenceVariation duplication = format.parse("p.Cys76_Glu79dup");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, duplication.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.GLUTAMIC_ACID, duplication.getLastChangingAminoAcid());
        Assert.assertEquals(76, duplication.getFirstChangingAminoAcidPos());
        Assert.assertEquals(79, duplication.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DUPLICATION, duplication.getSequenceChange().getType());
        Assert.assertEquals(79, duplication.getSequenceChange().getValue());
    }
}