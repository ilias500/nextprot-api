package org.nextprot.api.rdf.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller(value = "SparqlProxyController")
public class SparqlProxyController extends ServletWrappingProxyController 
{
	
	@Value("${sparql.url}")
	private String sparqlUrl;

	@Override
	String getRequestMapping() {
		return "/sparql";
	}

	@Override
	String getURLToBeProxied() {
		return sparqlUrl;
	}

}
