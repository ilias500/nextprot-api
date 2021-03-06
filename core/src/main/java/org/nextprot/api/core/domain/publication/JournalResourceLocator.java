package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObjectField;
import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.PublicationCvJournal;

import java.io.Serializable;

public class JournalResourceLocator extends BookResourceLocator implements Serializable {

    private static final long serialVersionUID = 0L;

    private CvJournal journal;

    @ApiObjectField(description = "The journal volume")
    private String volume;

    @ApiObjectField(description = "The journal issue")
    private String issue;

    public boolean hasJournalId() {
        return journal.hasJournalId();
    }

    public String getAbbrev() {
        return journal.getAbbrev();
    }

    public String getMedAbbrev() {
        return journal.getMedAbbrev();
    }

    public String getNLMid() {
        return journal.getNLMid();
    }

    /**
     * @return publication id or -1 if not exist
     */
    public long getPublicationId() {
        if (journal instanceof PublicationCvJournal)
            return ((PublicationCvJournal)journal).getPublicationId();
        return -1;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    PublicationType getPublicationType() {
        return PublicationType.ARTICLE;
    }

    public void setJournal(CvJournal journal) {
        this.journal = journal;
        this.setName(journal.getName());
    }
}
