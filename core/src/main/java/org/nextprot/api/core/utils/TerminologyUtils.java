package org.nextprot.api.core.utils;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.commons.utils.Tree.Node;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;

import static org.nextprot.api.commons.constants.TerminologyCv.EnzymeClassificationCv;
import static org.nextprot.api.commons.constants.TerminologyCv.NextprotCellosaurusCv;
import static org.nextprot.api.commons.constants.TerminologyCv.NextprotDomainCv;

//import org.nextprot.api.core.domain.TerminologyProperty;

public class TerminologyUtils {

	private static final Log LOGGER = LogFactory.getLog(TerminologyUtils.class);

	public static List<CvTerm.TermProperty> convertToProperties(String propliststring, Long termid, String termacc) {

		// Decomposes a pipe-separated string (generated by a SQL query) in a list property objects  containing  name/value pairs
		List<CvTerm.TermProperty> properties = new ArrayList<>();
		if (propliststring == null) return properties;
		
		// keep spaces in splitting pattern, since pipe alone can occur within fields
		List<String> allprop = Arrays.asList(propliststring.split(" \\| "));
		
		for (String propertystring : allprop) {
			// The splitter is ':=' since both ':' and '=' can occur alone within field
			List<String> currprop = Arrays.asList(propertystring.split(":="));
			if(currprop.size() != 2) {
			   String msg = "Problem with property in " + termacc + ": " + propertystring + " propString: " + propertystring;
			   LOGGER.warn(msg);
			   System.err.println(msg);
			   continue;
			}
			CvTerm.TermProperty property = new CvTerm.TermProperty();
			String propertyName = currprop.get(0) ;
			property.setPropertyName(propertyName);
			String propertyValue = currprop.get(1);
			property.setPropertyValue(propertyValue);
			property.settermId(termid);
			properties.add(property);
		}
		return properties;
	}
	
	public static List<DbXref> convertToXrefs (String xrefsstring) {

		if (xrefsstring == null) return null;
		// Builds DbXref list from String of xrefs formatted as "dbcat^ db^ acc^ id^ url^ link_url^ term_id^ term_name^ term_onto| ..."
		//                                               fields: 0      1   2    3   4    5         6        7          8
		List<DbXref> xrefs = new ArrayList<>();
		List<String> allxrefs = Arrays.asList(xrefsstring.split(" \\| "));
		for (String onexref: allxrefs) {			
			List<String> fields = Arrays.asList(onexref.split("\\^ "));

			DbXref dbref = new DbXref();

			dbref.setDatabaseCategory(fields.get(0));
			dbref.setDatabaseName(fields.get(1));
			dbref.setAccession(fields.get(2));
			dbref.setDbXrefId(Long.parseLong(fields.get(3)));

			String url = null;
			String linkurl = null;

			if (fields.size() > 4) {
				url = fields.get(4);
				if (fields.size() > 5)
					linkurl = fields.get(5);
			}

			if (url == null || url.isEmpty() || "none".equalsIgnoreCase(url)) {
				dbref.setUrl("None");
				dbref.setLinkUrl("None");
			}
			else {
				dbref.setUrl(url);
				dbref.setLinkUrl(linkurl);
			}
			
			if (fields.size() > 6 && ! fields.get(6).equals("-1")) {
				dbref.addProperty("term_id", fields.get(6), null);
				if (fields.size() > 7) dbref.addProperty("term_name", fields.get(7), null);
				if (fields.size() > 8) dbref.addProperty("term_ontology_display_name", fields.get(8), null);
			}
			
			
			xrefs.add(dbref);
		}

		return xrefs;
		
	}
	
	public static String convertPropertiesToString(List<CvTerm.TermProperty> properties) {

		if (properties == null) return null;
		// Build a String where propertyname/propertyvalue pairs are separated by pipes
		StringBuilder sb = new StringBuilder();
        int i = properties.size();
		for (CvTerm.TermProperty property : properties) {
			sb.append(property.getPropertyName());
			//sb.append(":=");
			sb.append(":");
			sb.append(property.getPropertyValue());
			if(--i != 0)
			  sb.append(" | ");
		}
		return sb.toString();
	}

	public static String convertXrefsToString(List<DbXref> xrefs) {

		if (xrefs == null) return null;
		// Build a String of xrefs formatted as "dbcat, db:acc" pairs separated by pipes
		StringBuilder sb = new StringBuilder();
        int i = xrefs.size();
		for (DbXref xref : xrefs) {
			sb.append(xref.getDatabaseCategory());
			sb.append(", ");
			sb.append(xref.getDatabaseName());
			sb.append(":");
			sb.append(xref.getAccession());
			if(--i != 0)
			  sb.append(" | ");
		}
		return sb.toString();
	}

	public static String convertXrefsToSolrString(List<DbXref> xrefs) {

		if (xrefs == null) return null;
		// Build a String of xrefs for solr formatted as "acc, db:acc" pairs separated by pipes
		StringBuilder sb = new StringBuilder();
        int i = xrefs.size();
		for (DbXref xref : xrefs) {
			sb.append(xref.getAccession());
			sb.append(", ");
			sb.append(xref.getDatabaseName());
			sb.append(":");
			sb.append(xref.getAccession());
			if(--i != 0)
			  sb.append(" | ");
		}
		return sb.toString();
	}


	public static Map<String, CvTerm> convertToTerminologyMap(List<CvTerm> terms) {
		Map<String, CvTerm> termMap = new HashMap<>();
		for(CvTerm term: terms){
			termMap.put(term.getAccession(), term);
		}
		return termMap;
	}
	
	static void populateTree(Tree.Node<CvTerm> currentNode, Map<String, CvTerm> termMap, int depth, final int maxDepth) {

		if(depth > maxDepth) return;

		if(depth > 100) throw new NextProtException("Getting stuck in building graph");
		
		if(currentNode.getValue() == null || currentNode.getValue().getChildAccession() == null || currentNode.getValue().getChildAccession().isEmpty()) {
			return;
		}
		
		for(String childAccession : currentNode.getValue().getChildAccession()){
			CvTerm childTerm = termMap.get(childAccession);
			if(childTerm != null) { // may be null in case of the terminology being another one like DO

				if(currentNode.getChildren() == null){
					currentNode.setChildren(new ArrayList<Tree.Node<CvTerm>>());
				}
				
				Tree.Node<CvTerm> childNode = new Tree.Node<CvTerm>(childTerm);
				childNode.setParents(Arrays.asList(currentNode));
				currentNode.getChildren().add(childNode);
				
				populateTree(childNode, termMap, depth+1, maxDepth);
				
			}
		}
		
	}
	
	public static List<Node<CvTerm>> getNodeListByName(Tree<CvTerm> tree, String accession) {
		List<Node<CvTerm>> result = new ArrayList<>();
		getNodeListByNameAndPopulateResult(result, tree.getRoot(), accession);
		return result;
		
	}

	private static void getNodeListByNameAndPopulateResult(List<Node<CvTerm>> currentResult, Node<CvTerm> node, String accession) {

		if(node.getValue().getAccession().equals(accession)){
				currentResult.add(node);
				return;
			}

		if(node.getChildren() != null && !node.getChildren().isEmpty()){
			for(Node<CvTerm> child : node.getChildren()){
				getNodeListByNameAndPopulateResult(currentResult, child, accession);
			}
		}
	}

	public static List<String> filterSynonyms(String ontology, String termName, String termDescription, String synonyms) {

		List<String> allsyn = Arrays.asList(synonyms.split("\\|"));

		List<String> finalSynonyms = new ArrayList<>();
		if(synonyms == null)
			return finalSynonyms;

		for(String currentSyn : allsyn){
			String synonym = currentSyn.trim();
			boolean skip = false;

			if(!TerminologyCv.valueOf(ontology).equals(NextprotCellosaurusCv)) {
				if((synonym).equals(termName)){
					skip = true;
				}
			}

			if(TerminologyCv.valueOf(ontology).equals(EnzymeClassificationCv)) {
				if(synonym == termDescription || termDescription.endsWith(synonym)){
					skip = true;
				}
			}else if(TerminologyCv.valueOf(ontology).equals(NextprotDomainCv)) {
				if((synonym + " DNA-binding domain").equals(termName)){
					skip = true;
				}else if((synonym + " domain").equals(termName)){
					skip = true;
				}else if((synonym + " repeat").equals(termName)){
					skip = true;
				}else if((synonym + " zinc finger").equals(termName)){
					skip = true;
				}
			}

			if(!skip){
				finalSynonyms.add(synonym);
			}
		}

		return finalSynonyms;

	}
}
