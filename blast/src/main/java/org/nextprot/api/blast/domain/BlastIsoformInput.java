package org.nextprot.api.blast.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.nextprot.api.blast.domain.gen.Description;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.ExceptionWithReason;

@JsonPropertyOrder({
        "isoform_accession",
        "begin",
        "end",
        "search_param",
        "isoform_description"
})
public class BlastIsoformInput extends BlastSequenceInput {

    private static final String ISOFORM_REX_EXP= "^NX_[^-]+-\\d+$";

    private String isoformAccession;
    private Integer querySeqBegin;
    private Integer querySeqEnd;
    private String entryAccession;
    private Description description;

    public BlastIsoformInput(String binPath, String nextprotBlastDbPath) {
        super(binPath, nextprotBlastDbPath);
    }

    @JsonIgnore
    public int getBeginPos() {
        return (querySeqBegin != null) ? querySeqBegin : 1;
    }

    @JsonIgnore
    public int getEndPos() {
        return (querySeqEnd != null) ? querySeqEnd : getSequence().length();
    }

    public int calcQuerySeqLength() {

        return getEndPos() - getBeginPos() + 1;
    }

    @JsonIgnore
    public String getEntryAccession() {
        return entryAccession;
    }

    public void setEntryAccession(String entryAccession) {
        this.entryAccession = entryAccession;
    }

    @JsonProperty("isoform_accession")
    public String getIsoformAccession() {
        return isoformAccession;
    }

    public void setIsoformAccession(String isoformAccession) {

        if (isoformAccession == null || !isoformAccession.matches(ISOFORM_REX_EXP)) {
            throw new NextProtException(isoformAccession+": invalid isoform accession (format: "+ISOFORM_REX_EXP+")");
        }

        this.isoformAccession = isoformAccession;
    }

    public Integer getQuerySeqBegin() {
        return querySeqBegin;
    }

    @JsonProperty("begin")
    public void setQuerySeqBegin(Integer querySeqBegin) {
        this.querySeqBegin = querySeqBegin;
    }

    @JsonProperty("end")
    public Integer getQuerySeqEnd() {
        return querySeqEnd;
    }

    public void setQuerySeqEnd(Integer querySeqEnd) {
        this.querySeqEnd = querySeqEnd;
    }

    @JsonIgnore
    public String getHeader() {

        return super.getHeader();
    }

    @JsonIgnore
    public String getSequence() {

        return super.getSequence();
    }

    @JsonProperty("isoform_description")
    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public void validateSequencePositions() throws ExceptionWithReason {

        if (getSequence() == null) {

            throw ExceptionWithReason.withMessage("missing query sequence");
        }

        if (querySeqBegin == null && querySeqEnd != null || querySeqBegin != null && querySeqEnd == null) {

            throw ExceptionWithReason.withMessage("begin: "+querySeqBegin+", end: "+querySeqEnd+": sequence positions should be both defined or undefined");
        }

        // both positions are defined
        if (querySeqBegin != null) {

            if (querySeqBegin > querySeqEnd) {
                throw ExceptionWithReason.withMessage("first sequence position "+querySeqBegin+" should be lower than last position "+querySeqEnd);
            }

            int seqLen = getSequence().length();

            // check positions
            if (querySeqBegin < 1 || querySeqBegin > seqLen) {

                throw ExceptionWithReason.withReason("first sequence position ", querySeqBegin + " is out of bound (should be > 0 and <= " + seqLen + ")");
            }
            if (querySeqEnd < 1 || querySeqEnd > seqLen) {

                throw ExceptionWithReason.withReason("last sequence position ", querySeqEnd + " is out of bound (should be > 0 and <= " + seqLen + ")");
            }
        }
    }
}
