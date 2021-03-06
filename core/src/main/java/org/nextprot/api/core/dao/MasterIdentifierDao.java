package org.nextprot.api.core.dao;

import java.util.List;

public interface MasterIdentifierDao {

	Long findIdByUniqueName(String uniqueName);
	
	List<String> findUniqueNamesOfChromosome(String chromosome);

	List<String> findUniqueNames();
	
	List<String> findUniqueNamesByGeneName(String geneName, boolean withSynonyms);
}
