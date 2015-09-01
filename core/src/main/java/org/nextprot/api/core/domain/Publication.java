package org.nextprot.api.core.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

@ApiObject(name = "publication", description = "A publication")
public class Publication implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;

	@ApiObjectField(description = "The neXtProt identifier of the publication")
	private Long id;

	@ApiObjectField(description = "The MD5 of the publication")
	private String md5;
	
	@ApiObjectField(description = "The title of the publication")
	private String title;

	@ApiObjectField(description = "The abstract text")
	private String abstractText;

	@ApiObjectField(description = "The journal volume")
	private String volume;

	@ApiObjectField(description = "The journal issue")
	private String issue;

	@ApiObjectField(description = "The first page")
	private String firstPage;

	@ApiObjectField(description = "The last page")
	private String lastPage;

	@ApiObjectField(description = "The type")
	private String publicationType;

	@ApiObjectField(description = "The publication date")
	private Date publicationDate;

	@ApiObjectField(description = "The publication date in text")
	private String textDate;

	@ApiObjectField(description = "The submission to db text (EMBL, PDB, ...")
	private String submission;

	@ApiObjectField(description = "Publications related to 15 entries or more")
	private Boolean isLargeScale;

	@ApiObjectField(description = "Curated Publications")
	private Boolean isCurated;
	// TODO: reassess the way we define 'curared/computed' and get rid of the 'limit 1' in publication-by-ressource.sql
	@ApiObjectField(description = "Computed Publications")
	private Boolean isComputed;
	
	public Boolean getIsLargeScale() {
		return isLargeScale;
	}

	public void setIsLargeScale(Boolean isLargeScale) {
		this.isLargeScale = isLargeScale;
	}

	public Boolean getIsCurated() {
		return isCurated;
	}

	public void setIsCurated(Boolean isCurated) {
		this.isCurated = isCurated;
	}

	public Boolean getIsComputed() {
		return isComputed;
	}

	public void setIsComputed(Boolean isComputed) {
		this.isComputed = isComputed;
	}

	@ApiObjectField(description = "The journal")
	protected CvJournal cvJournal;

	@ApiObjectField(description = "The list of authors")
	protected SortedSet<PublicationAuthor> authors;

	@ApiObjectField(description = "The associated cross references")
	protected Set<DbXref> dbXrefs;
	
	private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");  
	

	public long getPublicationId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMD5() {
		return md5;
	}

	public void setMD5(String md5) {
		this.md5 = md5;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubmission() {
		return submission;
	}

	public void setSubmission(String submission) {
		this.submission = submission;
	}

	public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public String getPublicationYear() {
		return this.yearFormat.format(this.publicationDate);
	}
	
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
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

	public String getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(String firstPage) {
		this.firstPage = firstPage;
	}

	public String getLastPage() {
		return lastPage;
	}

	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}
	
	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
	}

	public String getTextDate() {
		return textDate;
	}

	public void setTextDate(String textDate) {
		this.textDate = textDate;
	}

	public CvJournal getCvJournal() {
		return cvJournal;
	}

	public void setCvJournal(CvJournal cvJournal) {
		this.cvJournal = cvJournal;
	}

	public SortedSet<PublicationAuthor> getAuthors() {
		return authors;
	}

	public void setAuthors(SortedSet<PublicationAuthor> authors) {
		this.authors = authors;
	}

	public Set<DbXref> getDbXrefs() {
		return dbXrefs;
	}

	public void setDbXrefs(Set<DbXref> dbXrefs) {
		this.dbXrefs = dbXrefs;
	}

	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("id=");
		sb.append(this.id);
		sb.append("\n");
		sb.append("md5=");
		sb.append(this.md5);
		sb.append("\n");
		sb.append("title=");
		sb.append(this.title);
		sb.append("\n");
		sb.append("submission=");
		sb.append((this.submission != null) ? this.submission : "null");
		sb.append("\n");
		sb.append("volume=");
		sb.append(this.volume);
		sb.append("; issue=");
		sb.append(this.issue);
		sb.append("\n");
		sb.append("pub_type=");
		sb.append(this.publicationType);
		sb.append("\n");
		sb.append("journal=");
		sb.append(this.cvJournal);
		sb.append("\n");
		sb.append("authorsCnt=");
		sb.append((this.authors != null) ? this.authors.size() : "null");
		sb.append("\n");
		
		return sb.toString();

	}


}
