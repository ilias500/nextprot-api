package org.nextprot.api.solr.service;


import org.apache.solr.client.solrj.SolrQuery;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;

public interface SolrService {
	
	String indexTerminologies();
	String indexPublications();
	String initIndexEntries(boolean isGold);
	String indexEntriesChromosome(boolean isGold, String chrName);
	String indexEntry(String entryAccession, boolean isGold);

	/**
	 * Execute a SOLR search query and return results
	 *
	 * @param query
	 */
	SearchResult executeQuery(Query query) throws SearchQueryException;

	/**
	 * Returns only the IDs of the document which are the result of the query
	 *
	 * @param query
	 * @return
	 * @throws SearchQueryException
	 */
	SearchResult executeIdQuery(Query query) throws SearchQueryException;

	/**
	 * Verifies if the specified name matches a name of a registered index
	 *
	 * @param indexName
	 * @return
	 */
	boolean checkAvailableIndex(String indexName);

	Query buildQueryForAutocomplete(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter);

	Query buildQueryForSearchIndexes(String indexName, String configurationName, QueryRequest request);

	Query buildQueryForProteinLists(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter);

	SolrQuery buildSolrIdQuery(Query query, IndexConfiguration indexConfig) throws SearchQueryException;
}
