package org.nextprot.api.rdf.controller;

import java.util.List;

import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller used many to log and tune queries. Check the log times. No cache is used on purpose
 * 
 * @author dteixeira
 */
@Lazy
@Controller
public class SparqlController {

	private static final String PREFIX_TEMPLATE = "prefix";

	@Autowired
	private SparqlDictionary sparqlDictionary;

	
	@RequestMapping(value ="/sparql-prefixes", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public List<String> sparqlPrefixes() {
		return sparqlDictionary.getSparqlPrefixesList();
	}
	

	@RequestMapping(value ="/common-prefixes", produces = {"text/turtle"})
	public String commonPrefixes(Model model) {
		return PREFIX_TEMPLATE; 
	}
	
}
