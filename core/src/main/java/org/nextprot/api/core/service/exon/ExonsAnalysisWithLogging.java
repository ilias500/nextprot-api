package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.exon.ExonCategory;
import org.nextprot.api.core.domain.exon.SimpleExon;

/**
 * A logger for TranscriptExonsAnalyser
 *
 * Created by fnikitin on 22/07/15.
 */
public class ExonsAnalysisWithLogging implements ExonsAnalysis {

    private StringBuilder sb;

    @Override
    public void started() {
        sb = new StringBuilder();
    }

    @Override
    public void startedExon(SimpleExon exon) {}

    @Override
    public void analysedCodingExon(SimpleExon exon, AminoAcid first, AminoAcid last, ExonCategory category) {

        ExonsAnalysis.super.analysedCodingExon(exon, first, last, category);

        sb.append(first.getPosition());
        sb.append("[");
        sb.append(getAACode(first, true)).append("(+").append(first.getPhase()).append(")--");
        sb.append(category).append("--");
        sb.append(getAACode(last, false)).append("(+").append(last.getPhase()).append(")");
        sb.append("]");
        sb.append(last.getPosition()).append(" ");
    }

    @Override
    public void analysedCodingExonFailed(SimpleExon exon, ExonOutOfIsoformBoundException exonOutOfIsoformBoundException) {

        AminoAcid first = exonOutOfIsoformBoundException.getFirst();

        if (exonOutOfIsoformBoundException.getAminoAcidOutOfBound() == ExonOutOfIsoformBoundException.AminoAcidOutOfBound.LAST) {
            sb.append(first.getPosition());
            sb.append("[");
            sb.append(getAACode(first, true)).append("(+").append(first.getPhase()).append(")--");
            sb.append("OUT-OF-BOUND--");
            sb.append("NA");
            sb.append("]!");
            sb.append(exonOutOfIsoformBoundException.getLast().getPosition()).append(">").append(exonOutOfIsoformBoundException.getIsoformLength()).append("!");
        } else {
            sb.append(first.getPosition());
            sb.append("!");
            sb.append(exonOutOfIsoformBoundException.getFirst().getPosition()).append(">").append(exonOutOfIsoformBoundException.getIsoformLength()).append("!");
            sb.append("[");
            sb.append(getAACode(first, true)).append("(+").append(first.getPhase()).append(")--");
            sb.append("OUT-OF-BOUND--");
            sb.append("NA");
            sb.append("]!");
            sb.append(exonOutOfIsoformBoundException.getLast().getPosition()).append(">").append(exonOutOfIsoformBoundException.getIsoformLength()).append("!");
        }
    }

    @Override
    public void analysedNonCodingExon(SimpleExon exon, ExonCategory category) {

        ExonsAnalysis.super.analysedNonCodingExon(exon, category);

        sb.append(category.getTypeString()).append(" ");
    }

    @Override
    public void terminated(SimpleExon exon) {}

    @Override
    public void terminated() {}

    public String getMessage() {

        return sb.toString();
    }

    private String getAACode(AminoAcid aa, boolean first) {

        if (aa.getCode()== null) {
            return "?";
        }

        String aa3code = aa.getCode().get3LetterCode();

        int phase = aa.getPhase();

        if (phase == 0) {
            return aa3code;
        }
        else {
            return (first) ? aa3code.substring(phase) : aa3code.substring(0, phase);
        }
    }
}
