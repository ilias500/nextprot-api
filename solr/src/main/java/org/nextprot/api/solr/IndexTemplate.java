package org.nextprot.api.solr;



public abstract class IndexTemplate extends AbstractSolrIndex {

	protected IndexTemplate(String name, String url) {
		super(name, url);
		setupConfigurations();
	}

	protected abstract void setupConfigurations();
	
	public abstract IndexField[] getFieldValues();
	
	public static abstract class ConfigurationName { };
}
