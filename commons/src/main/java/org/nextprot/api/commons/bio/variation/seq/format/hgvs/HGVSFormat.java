package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.format.ChangingAAsFormat;

/**
 * HGVS implementation of mutated aas
 *
 * Created by fnikitin on 07/09/15.
 */
public class HGVSFormat implements ChangingAAsFormat {

    @Override
    public void format(StringBuilder sb, SequenceVariation sequenceVariation, AminoAcidCode.AACodeType type) {

        sb.append("p.");

        sb.append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getFirstChangingAminoAcid()));
        sb.append(sequenceVariation.getFirstChangingAminoAcidPos());
        if (sequenceVariation.isAminoAcidRange())
            sb.append("_").append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getLastChangingAminoAcid())).append(sequenceVariation.getLastChangingAminoAcidPos());
    }
}
