package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.commons.utils.Tree.Node;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.core.utils.graph.OntologyDAG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
class TerminologyServiceImpl implements TerminologyService {

	private static final Log LOGGER = LogFactory.getLog(TerminologyServiceImpl.class);

	@Autowired
	private TerminologyDao terminologyDao;

	@Override
	@Cacheable("terminology-by-accession")
	public CvTerm findCvTermByAccession(String accession) {
		return terminologyDao.findTerminologyByAccession(accession);
	}

	private static void appendAncestor(Node<CvTerm> node, Set<String> result) {
		result.add(node.getValue().getAccession());
		if (node.getParents() != null && !node.getParents().isEmpty()) {
			for (Node<CvTerm> parent : node.getParents()) {
				appendAncestor(parent, result);
			}
		}

	}

	//TODO TRY TO PLACE THIS ELSEWHERE, BUT PROBABLY SHOULD BE CACHED!
	@Cacheable("terminology-ancestor-sets")
	public Set<String> getAncestorSets(List<Tree<CvTerm>> trees, String accession) {
		Set<String> result = new TreeSet<String>();
		
		for(Tree<CvTerm> tree : trees){
			List<Node<CvTerm>> nodes = TerminologyUtils.getNodeListByName(tree, accession);
			for (Node<CvTerm> node : nodes) {
				appendAncestor(node, result);
			}
		}

		result.remove(accession); // a term is not it's own ancestor
		return result;
	}

	@Override
	@Cacheable("terminology-tree-depth")
	public Terminology findTerminology(TerminologyCv terminologyCv) {
		List<CvTerm> terms = findCvTermsByOntology(terminologyCv.name());
		List<Tree<CvTerm>> result = TerminologyUtils.convertCvTermsToTerminology(terms, 1000);
		return new Terminology(result, terminologyCv);
	}

	@Override
	@Cacheable("terminology-by-ontology")
	public List<CvTerm> findCvTermsByOntology(String ontology) {
		List<CvTerm> terms = terminologyDao.findTerminologyByOntology(ontology);
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<CvTerm>().addAll(terms).build();
	}

	@Override
	@Cacheable("ontology-dag")
	public OntologyDAG findOntologyGraph(TerminologyCv terminologyCv) {

		return new OntologyDAG(terminologyCv, this);
	}

	@Override
	@Cacheable("terminology-all")
	public List<CvTerm> findAllCVTerms() {
		List<CvTerm> terms = terminologyDao.findAllTerminology();
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<CvTerm>().addAll(terms).build();

	}

	@Override
	@Cacheable("enzyme-terminology") // TODO there should be an utiliy method on
										// entry to get the enzymes...
	public List<CvTerm> findEnzymeByMaster(String entryName) {
		Set<String> accessions = new HashSet<String>(terminologyDao.findEnzymeAcsByMaster(entryName));
		if (!accessions.isEmpty()) { // is found accessions gets the
										// corresponding terminology
			List<CvTerm> terms = terminologyDao.findTerminologyByAccessions(accessions);
			// returns a immutable list when the result is cacheable (this
			// prevents modifying the cache, since the cache returns a
			// reference) copy on read and copy on write is too much time
			// consuming
			return new ImmutableList.Builder<CvTerm>().addAll(terms).build();
		} else
			return new ImmutableList.Builder<CvTerm>().build(); // returns
																		// empty
																		// list
	}

	@Override
	public List<CvTerm> findCvTermsByAccessions(Set<String> terminologyAccessions) {
		List<CvTerm> terms = terminologyDao.findTerminologyByAccessions(terminologyAccessions);
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<CvTerm>().addAll(terms).build();

	}

	@Override
	@Cacheable("terminology-names")
	public List<String> findTerminologyNamesList() {
		return new ImmutableList.Builder<String>().addAll(terminologyDao.findTerminologyNamesList()).build();
	}
}
