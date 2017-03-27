package org.nextprot.api.etl.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.etl.service.ConsistencyService;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;

@Service
public class ConsistencyServiceImpl implements ConsistencyService{

	@Autowired PublicationService publicationService;
	@Autowired TerminologyService terminologyService;
	@Autowired StatementDao statementDao;
	
	@Override
	public List<String> findMissingPublications() {
		
		List<String> missingPublications = new ArrayList<>();
		
		List<String> pubmedIds = statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField.REFERENCE_ACCESSION, StatementField.REFERENCE_DATABASE, "PubMed");
		List<String> doisIds = statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField.REFERENCE_ACCESSION, StatementField.REFERENCE_DATABASE, "DOI");
		
		for(String pId : pubmedIds) {
			if(pId != null){ 
				Publication pub = publicationService.findPublicationByDatabaseAndAccession("PubMed", pId);
				if(pub == null){
					missingPublications.add("PubMed" + pId);
				}
			}
		};

		for(String dId : doisIds) {
			if(dId != null){ 
				Publication pub = publicationService.findPublicationByDatabaseAndAccession("DOI", dId);
				if(pub == null){
					missingPublications.add("DOI" + dId);
				}
			}
		};

		return missingPublications;
	}

	@Override
	public List<String> findMissingCvTerms() {
		

		List<String> missingCvTerms = new ArrayList<>();
		
		List<String> terms = statementDao.findAllDistinctValuesforField(StatementField.ANNOT_CV_TERM_ACCESSION);
		for(String t : terms) {
			if(t != null){
				CvTerm term = terminologyService.findCvTermByAccession(t);
				if(term == null){
					missingCvTerms.add(t);
				}
			}
		}
		
		return missingCvTerms;

	}


}
