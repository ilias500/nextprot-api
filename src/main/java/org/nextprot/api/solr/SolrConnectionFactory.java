package org.nextprot.api.solr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.nextprot.api.commons.exception.SearchConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class SolrConnectionFactory {
	private Map<String, HttpSolrServer> serverMap;
	private final String baseSolrUrl;
	
	private final char DASH = '/';
	
	@Autowired
	public SolrConnectionFactory(final SolrConfiguration solrConfiguration, @Value("${solr.url}") final String baseSolrUrl) {

		if(baseSolrUrl.charAt(baseSolrUrl.length()-1) != DASH)
			this.baseSolrUrl = baseSolrUrl + DASH;
		else this.baseSolrUrl = baseSolrUrl;
		
		final List<SolrIndex> indexes = solrConfiguration.getIndexes();
		this.serverMap = new HashMap<String, HttpSolrServer>();

		for(SolrIndex index : indexes) {
			this.serverMap.put(index.getName(), new HttpSolrServer(this.baseSolrUrl+index.getUrl()));
		}
	}
	
	public SolrServer getServer(String indexName) {
		if(this.serverMap.containsKey(indexName)) 
			return this.serverMap.get(indexName);
		else throw new SearchConfigException("Index "+indexName+" is not available");
	}
	
}
