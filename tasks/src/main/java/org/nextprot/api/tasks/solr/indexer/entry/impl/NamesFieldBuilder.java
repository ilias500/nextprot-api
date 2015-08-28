package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.Overview.EntityName;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class NamesFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {
		
		Overview ovv = entry.getOverview();
		
		//TODO Daniel repeated code in CvFieldBuilder
		Set <String> cv_acs = new HashSet<String>();
		for (Annotation currannot : entry.getAnnotations()) {
			String category = currannot.getCategory();
			if(!category.equals("tissue specificity")) {
				String cvac = currannot.getCvTermAccessionCode();
				if (cvac != null) {
					cv_acs.add(cvac); 
				}
			}
		}

		
		List <EntityName> altnames = null;
		altnames = ovv.getProteinNames();
		if(altnames != null )
			for (EntityName altname : altnames) {
				List <EntityName> paltnames = altname.getSynonyms();
				if(paltnames != null )
				for (EntityName paltfullname : paltnames) {
				    addField(Fields.ALTERNATIVE_NAMES, paltfullname.getName());
				    List <EntityName> paltshortnames = paltfullname.getSynonyms();
				    if(paltshortnames != null )
				    for (EntityName paltshortname : paltshortnames) {
				    	addField(Fields.ALTERNATIVE_NAMES, paltshortname.getName());
				    }
				}
			}
		
		altnames = ovv.getAdditionalNames(); // special names (INN, allergens)
		if(altnames != null )
			for (EntityName altname : altnames) {
				addField(Fields.ALTERNATIVE_NAMES, altname.getName());
			}
		
		altnames = ovv.getFunctionalRegionNames(); // The enzymatic activities of a multifunctional enzyme (maybe redundent with getEnzymes)
		if(altnames != null )
			for (EntityName altname : altnames) {
				addField(Fields.REGION_NAME, altname.getName()); // region_name should be renamed activity_name
				// Synonyms allready collected in the getEnzymes loop
				// List <EntityName> paltnames = altname.getSynonyms();
				// if(paltnames != null )
				// for (EntityName ecname : paltnames) {
				    //doc.addField("ec_name", ecname.getName());
				//	System.err.println(id + " fromincludes: " + ecname.getName());
				//} 
			}
		
		// Gene names, synonyms and orf names
		List <EntityName> genenames = ovv.getGeneNames();
		if(genenames != null ) {
			String maingenename = ovv.getMainGeneName(); // TODO: check for multigene entries
			addField(Fields.RECOMMENDED_GENE_NAMES, maingenename);
			addField(Fields.RECOMMENDED_GENE_NAMES_S, maingenename);
			for (EntityName currname : genenames) {
				List <EntityName> genesynonames = currname.getSynonyms();
				if(genesynonames != null)
				for (EntityName genesynoname : genesynonames) {
				addField(Fields.ALTERNATIVE_GENE_NAMES, genesynoname.getName());
			    //System.err.println("syn: " + genesynoname.getName()); 
				}
			}
		}
		//else System.err.println("no gene names for: " + id);
		
		List<Family> families = ovv.getFamilies();
		String allfamilies = null;
		for (Family family : families) { // alternatively use a multivalue solr field
			if (allfamilies == null){
				allfamilies = family.getName();
			} else {
				allfamilies += " | " + family.getName();
			}
		}
		if (allfamilies == null) {
			addField(Fields.FAMILY_NAMES, allfamilies);
			addField(Fields.FAMILY_NAMES_S, allfamilies);
		}

		//TODO ORF NAMES are missing
	
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ORF_NAMES, Fields.FAMILY_NAMES, Fields.FAMILY_NAMES_S, Fields.ALTERNATIVE_GENE_NAMES, Fields.RECOMMENDED_GENE_NAMES, Fields.RECOMMENDED_GENE_NAMES_S, Fields.REGION_NAME, Fields.ALTERNATIVE_NAMES);
	}

}
