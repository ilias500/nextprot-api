package org.nextprot.api.web.service.impl;

import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Ignore
public class GenomicMappingTest extends WebIntegrationBaseTest {

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Test
	public void testAllEntries() throws Exception {

		Set<String> names = masterIdentifierService.findUniqueNames();
		for (String name : names) {
			entryBuilderService.build(EntryConfig.newConfig(name).withGenomicMappings());
		}
	}

	@Test
	public void testNX() throws Exception {

		Set<String> names = Sets.newHashSet("NX_Q5JQC4");
		for (String name : names) {
			entryBuilderService.build(EntryConfig.newConfig(name).withGenomicMappings());
		}
	}
}