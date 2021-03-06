package org.nextprot.api.core.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import java.io.Serializable;

@ApiObject(name = "identifier", description = "The identifier")
public class Identifier implements Serializable{

	private static final long serialVersionUID = 2L;

	@ApiObjectField(description = "The identifier name")
	private String name;
	@ApiObjectField(description = "The identifier type")
	private String type;
	@ApiObjectField(description = "The identifier database")
	private String database;
	@ApiObjectField(description = "The database category")
	private String category;
	private String link;
	
/*
	private String id;
	private Long synonymId;
	private Long xrefId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getSynonymId() {
		return synonymId;
	}
	public void setSynonymId(Long syn_id) {
		this.synonymId = syn_id;
	}
	public Long getXrefId() {
		return xrefId;
	}
	public void setXrefId(Long xref_id) {
		this.xrefId = xref_id;
	}
*/
	
	
	
	public String getName() {
		return name;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

    public String getDatabaseCategory() {
        return category;
    }

    public void setDatabaseCategory(String category) {
        this.category = category;
    }

    /**
	 * Returns a string specifying the provenance of the identifier
	 * @return the database if not null otherwise returns the type
	 */
	public String getProvenance() {
		if (database!=null) return database;
		return type;
	}
	
	
}
