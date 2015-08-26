package org.nextprot.api.tasks.solr.indexer.entry.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;


public class ChromosomeFieldBuilderIntegrationTest extends SolrIntegrationTest{

	@Autowired	private EntryBuilderService entryBuilderService = null;
	@Autowired	private MasterIdentifierService masterIdentifierService = null;

	@Test
	public void testChrLoc() {
		
		Fields field = Fields.CHR_LOC;
		String entryName = "NX_Q06124";
		
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withChromosomalLocations());

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder(entry);
		String chrLocValue = cfb.build(entry, field, String.class);
		
		assertTrue(chrLocValue.contains("12q24.13"));

	}
}
