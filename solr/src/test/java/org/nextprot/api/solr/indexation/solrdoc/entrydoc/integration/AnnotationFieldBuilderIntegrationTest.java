package org.nextprot.api.solr.indexation.solrdoc.entrydoc.integration;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.core.EntrySolrField;
import org.nextprot.api.solr.indexation.solrdoc.entrydoc.AnnotationSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nextprot.api.solr.indexation.solrdoc.entrydoc.SolrDiffTest.getFieldValue;


public class AnnotationFieldBuilderIntegrationTest extends org.nextprot.api.solr.indexation.solrdoc.entrydoc.SolrBuildIntegrationTest {

	@Autowired	private EntryBuilderService entryBuilderService = null;
	@Test
	public void testBEDintegration() {
		String[] BEDtest_list = {"NX_P35498", "NX_Q99250","NX_Q9NY46", "NX_P35499", "NX_Q14524", "NX_Q01118","NX_Q9UQD0", "NX_Q15858", "NX_Q9Y5Y9", "NX_Q9UI33",
				"NX_P38398", "NX_P51587","NX_P16422", "NX_P40692", "NX_Q9UHC1", "NX_P43246", "NX_P52701", "NX_P54278"};
		int bedAnnotCnt = 0;
		
		AnnotationSolrFieldCollector afb = new AnnotationSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		afb.collect(fields, getEntry("NX_P35498"), false);

		List<String> annotations = getFieldValue(fields, EntrySolrField.ANNOTATIONS, List.class);
		for(String annot : annotations) {
			if(annot.startsWith("SCN1A-")) {
				bedAnnotCnt++;
				// System.err.println("BED annot: " + annot);
			}
			
		}
		Assert.assertTrue(bedAnnotCnt >= 33);
		
		bedAnnotCnt = 0;
		fields.clear();
		afb.collect(fields, getEntry("NX_P16422"), false);
		annotations = getFieldValue(fields, EntrySolrField.ANNOTATIONS, List.class);
		for(String annot : annotations)
			if(annot.startsWith("EPCAM-"))
				bedAnnotCnt++;
		
		Assert.assertTrue(bedAnnotCnt >= 21);
	}
	
	protected Entry getEntry(String entryName){
		return entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());
	}
	
}
