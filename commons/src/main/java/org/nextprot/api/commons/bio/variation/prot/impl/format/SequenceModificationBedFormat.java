package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.format.SingleModificationBEDFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.varseq.format.AminoAcidModificationBEDFormatter;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeFormat;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormat;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class SequenceModificationBedFormat extends SequenceVariationFormat {

    private final AminoAcidModificationBEDFormatter aminoAcidModificationFormatter;
    private final Map<SequenceChange.Type, SequenceChangeFormat> changeFormats;

    public SequenceModificationBedFormat() {

        aminoAcidModificationFormatter = new AminoAcidModificationBEDFormatter();
        changeFormats = new EnumMap<>(SequenceChange.Type.class);
        changeFormats.put(SequenceChange.Type.PTM, new SingleModificationBEDFormat());
    }

    @Override
    protected AminoAcidModificationBEDFormatter getChangingSequenceFormatter() {

        return aminoAcidModificationFormatter;
    }

    @Override
    protected SequenceChangeFormat getSequenceChangeFormat(SequenceChange.Type changeType) {

        return changeFormats.get(changeType);
    }

    @Override
    protected Collection<SequenceChange.Type> getAvailableChangeTypes() {

        return changeFormats.keySet();
    }

    @Override
    public String format(SequenceVariation variation, AminoAcidCode.CodeType type) {

        StringBuilder sb = new StringBuilder();

        changeFormats.get(variation.getSequenceChange().getType()).format(sb, variation.getSequenceChange(), type);

        aminoAcidModificationFormatter.format(variation, type, sb);

        return sb.toString();
    }
}
