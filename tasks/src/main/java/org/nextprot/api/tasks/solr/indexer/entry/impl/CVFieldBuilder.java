package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.*;
import java.util.stream.Collectors;

@EntryFieldBuilder
public class CVFieldBuilder extends FieldBuilder {

	protected static final Logger LOGGER = Logger.getLogger(CVFieldBuilder.class);

	@Override
	protected void init(Entry entry) {

		boolean buildingSilverIndex = !this.isGold();

		Set <String> cvTermsSetForAncestors = new HashSet<>();

		//Get cv terms related to normal annotations (except expressions)
		cvTermsSetForAncestors.addAll(setAndGetCvTermAnnotationsExceptExpression(entry, buildingSilverIndex));

		//Get family names
		cvTermsSetForAncestors.addAll(setAndGetFamilyNames(entry));

		//Only cv terms from normal annotations and family are required to be indexed with their ancestors
		setAncestorsAndSynonyms(entry, cvTermsSetForAncestors);

		//Add more cv term accession
		setExperimentalContextAndPropertiesCvAccessionOnly(entry);

		//Add enzyme names to EC_NAMES
		setEnzymeNames(entry);

	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.CV_ANCESTORS_ACS, Fields.CV_ANCESTORS, Fields.CV_SYNONYMS, Fields.CV_NAMES, Fields.CV_ACS, Fields.EC_NAME);
	}


	private Set <String> setAndGetCvTermAnnotationsExceptExpression(Entry entry, boolean buildingSilverIndex){

		Set <String> cv_acs = new HashSet<>();

		// CV accessions
		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();

			//Expression are set on another field, because we don't want to give them so much importance as normal annotations such as go
			//Example: An entry is almost always tested in all tissues, therefore if we would type in search for "liver" we would get as much as importance as proteins with function in liver
			if(!category.equals("tissue specificity")) { // tissue-specific CVs are indexed under 'expression'
				if(getCvTermFromAnnot(currannot).isPresent()){
					CvTerm term = getCvTermFromAnnot(currannot).get();

					//If there is no terms or tjey are all negative don't index
					if (AnnotationUtils.onlyNegativeEvidences(currannot))
						continue;

					//If we are building SILVER index always add, otherwise (we are building GOLD index) we need the annotation need to be GOLD.
					if(buildingSilverIndex || currannot.getQualityQualifier().equals("GOLD")) {
						addField(Fields.CV_ACS, term.getAccession());
						addField(Fields.CV_NAMES,  term.getName());
						cv_acs.add(term.getAccession()); // No duplicates: this is a Set, will be used for synonyms and ancestors
					}
				}

			}
		}

		return cv_acs;
	}

	private void setExperimentalContextAndPropertiesCvAccessionOnly(Entry entry){

		Map<Long, List<CvTerm>> expCtxtCvTermMap = extractCvTermsFromExperimentalContext(entry);
		//We have added in CV_ACS the accessions related to experimental context and properties
		for (Annotation annot : entry.getAnnotations()) {

			//Check cv terms used in experimental context
			List<CvTerm> terms = new ArrayList<>();
			//Check cv terms used in experimental context
			terms.addAll(extractCvTermsFromExperimentalContext(annot, expCtxtCvTermMap));
			terms.addAll(extractCvTermsFromProperties(annot));


			for (CvTerm t : terms) {
				//Only add accessions in here. The use case is related to the page /term/TERM-NAME and see entries related to the term.
				//No need to index term name in here
				addField(Fields.CV_ACS, t.getAccession());
			}
		}
	}

	private Set <String> setAndGetFamilyNames(Entry entry){

		Set <String> cv_acs = new HashSet<>();

		// Families (why not part of Annotations ?)
		for (Family family : entry.getOverview().getFamilies()) {
			addField(Fields.CV_ACS, family.getAccession());
			addField(Fields.CV_NAMES,  family.getName() + " family");
			cv_acs.add(family.getAccession());
		}

		return cv_acs;

	}


	private void setAncestorsAndSynonyms(Entry entry, Set <String> cv_acs){

		// top level ancestors (Annotation, feature, and ROI)
		final Set <String> TOP_ACS = new HashSet<>(Arrays.asList("CVAN_0001","CVAN_0002","CVAN_0011"));

		Set <String> cv_synonyms = new HashSet<String>();
		Set <String> cv_ancestors_acs = new HashSet<String>();

		// Final CV acs, ancestors and synonyms
		for (String cvac : cv_acs) {
			CvTerm term = this.terminologyservice.findCvTermByAccession(cvac);
			if (null==term) {
				LOGGER.error(entry.getUniqueName() + " - term with accession |" + cvac + "| not found with findCvTermByAccession()");
				continue;
			}
			List<String> ancestors = terminologyservice.getAllAncestorsAccession(term.getAccession());
			if(ancestors != null) {
				for (String ancestor : ancestors) cv_ancestors_acs.add(ancestor);
			}
			List<String> synonyms = term.getSynonyms();
			if(synonyms != null) {
				for (String synonym : synonyms) cv_synonyms.add(synonym.trim()); // No duplicate: this is a Set
			}
		}

		// Remove uninformative top level ancestors (Annotation, feature, and ROI)
		cv_ancestors_acs.removeAll(TOP_ACS);

		// Index generated sets
		for (String ancestorac : cv_ancestors_acs) {
			addField(Fields.CV_ANCESTORS_ACS, ancestorac);
			addField(Fields.CV_ANCESTORS, this.terminologyservice.findCvTermByAccessionOrThrowRuntimeException(ancestorac).getName());
		}

		for (String synonym : cv_synonyms) {
			addField(Fields.CV_SYNONYMS, synonym);
		}

	}

	private void setEnzymeNames(Entry entry){

		List<CvTerm> enzymes = entry.getEnzymes();
		String ec_names = "";
		for (CvTerm currenzyme : enzymes) {
			addField(Fields.CV_NAMES, currenzyme.getName());
			if(ec_names != "") ec_names += ", ";
			ec_names += "EC " + currenzyme.getAccession();
			List <String> synonyms = currenzyme.getSynonyms();
			if(synonyms != null)
				for (String synonym : synonyms) {
					addField(Fields.CV_SYNONYMS, synonym.trim());
				}
		}

		addField(Fields.EC_NAME, ec_names);

	}



	// PRIVATE METHODS

	static Map<Long, List<CvTerm>> extractCvTermsFromExperimentalContext(Entry entry) {
		Map<Long, List<CvTerm>> expCtxtCvTermMap = new HashMap<>();

		for (ExperimentalContext expCtxt : entry.getExperimentalContexts()) {

			List<CvTerm> contextTerms = new ArrayList();
			if(expCtxt.getDisease() != null) contextTerms.add(expCtxt.getDisease());
			if(expCtxt.getTissue() != null) contextTerms.add(expCtxt.getTissue());
			if(expCtxt.getDevelopmentalStage() != null) contextTerms.add(expCtxt.getDevelopmentalStage());
			if(expCtxt.getCellLine() != null) contextTerms.add(expCtxt.getCellLine());
			if(expCtxt.getOrganelle() != null) contextTerms.add(expCtxt.getOrganelle());
			if(expCtxt.getDetectionMethod() != null) contextTerms.add(expCtxt.getDetectionMethod());
			if(!contextTerms.isEmpty()){
				expCtxtCvTermMap.put(expCtxt.getContextId(), contextTerms);
			}
		}

		return expCtxtCvTermMap;
	}

	static Optional<CvTerm> getCvTermFromAnnot(Annotation annot) {

		if(annot.getCvTermAccessionCode() != null){
			CvTerm term = new CvTerm();
			term.setAccession(annot.getCvTermAccessionCode());
			term.setName(annot.getCvTermName());
			return Optional.of(term);
		}

		return Optional.empty();


	}

	static List<CvTerm> extractCvTermsFromExperimentalContext(Annotation annot, Map<Long, List<CvTerm>> expCtxtCvTermMap) {

		List<CvTerm> terms = new ArrayList<>();

		//Don't get negative evidences
		List<Long> ctxtIds = annot.getEvidences().stream()
				.filter(e -> !e.isNegativeEvidence())
				.map(e -> e.getExperimentalContextId())
				.collect(Collectors.toList());

		for (Long ctxtId : ctxtIds) {
			List<CvTerm> ts = expCtxtCvTermMap.get(ctxtId);
			if(ts != null){
				for (CvTerm t : ts) {
					terms.add(t);
				}
			}
		}
		return terms;
	}


	static List<CvTerm> extractCvTermsFromProperties(Annotation annot) {

		if(AnnotationUtils.onlyNegativeEvidences(annot)) {
			return new ArrayList<>();
		}

		List<CvTerm> terms = new ArrayList<>();
		for(AnnotationProperty ap : annot.getProperties()){
			if((ap != null) && (ap.getName() != null) && (ap.getName().equals("topology") || ap.getName().equals("orientation"))){
				CvTerm term = new CvTerm();
				term.setAccession(ap.getAccession());
				term.setName(ap.getValue());
				terms.add(term);
			}
		}
		return terms;
	}


}
