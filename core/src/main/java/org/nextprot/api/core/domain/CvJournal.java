package org.nextprot.api.core.domain;

import java.io.Serializable;

public class CvJournal implements Serializable{

	private static final long serialVersionUID = -8793476383094626534L;

	private Long journalId;	// Internal journal id
	private String name;	// Official journal name
	private String abbrev;	// Medline journal abbreviation
	private String nlmid;	// The National Library of Medicine id, eg: http://www.ncbi.nlm.nih.gov/nlmcatalog/?term=0404511
	// WE are missing issn and e-isnn
	
	public Long getJournalId() {
		return journalId;
	}

	public void setJournalId(Long journalId) {
		this.journalId = journalId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAbbrev() {
		return abbrev;
	}
	
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	
	public String getNLMid() {
		return nlmid;
	}
	
	public void setNLMid(String nlmid) {
		this.nlmid = nlmid;
	}
	
	public String toString() {
		return "(id=" + journalId + ") " + name + "(short=" + abbrev + ")";
	}
	
}
