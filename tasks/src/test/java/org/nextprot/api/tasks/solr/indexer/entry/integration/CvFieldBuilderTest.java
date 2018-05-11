package org.nextprot.api.tasks.solr.indexer.entry.integration;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrBuildIntegrationTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.CVFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class CvFieldBuilderTest extends SolrBuildIntegrationTest{


	@Autowired	private EntryBuilderService entryBuilderService ;
	@Autowired	private TerminologyService terminologyService;

	@Test
	public void shouldContainCvTermsFromExperimentalContext() {

		String entryName = "NX_Q9H207";
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withEnzymes().with("variant"));
		CVFieldBuilder cvfb = new CVFieldBuilder();
		cvfb.setTerminologyService(terminologyService);

		cvfb.initializeBuilder(entry);
		List<String> cvAvs = cvfb.getFieldValue(Fields.CV_ACS, List.class);
        List<String> cvNames = cvfb.getFieldValue(Fields.CV_NAMES, List.class);

		assertTrue(cvAvs.contains("ECO:0000219"));
		assertTrue(cvNames.contains("nucleotide sequencing assay evidence"));

    }


	@Test
	public void shouldContainCvTermsFromPropertyNamesSuchAsTopologyAndOrientation() {

		String entryName = "NX_Q9BV57";
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withEnzymes().with("subcellular-location"));

		CVFieldBuilder cvfb = new CVFieldBuilder();
		cvfb.setTerminologyService(terminologyService);

		cvfb.initializeBuilder(entry);
		List<String> cvAvs = cvfb.getFieldValue(Fields.CV_ACS, List.class);
		List<String> cvNames = cvfb.getFieldValue(Fields.CV_NAMES, List.class);

		assertTrue(cvAvs.contains("SL-9910"));
		assertTrue(cvNames.contains("Cytoplasmic side"));
		assertTrue(cvAvs.contains("SL-9903"));
		assertTrue(cvNames.contains("Peripheral membrane protein"));
	}
}
