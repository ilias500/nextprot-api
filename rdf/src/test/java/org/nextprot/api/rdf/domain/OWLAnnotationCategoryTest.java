package org.nextprot.api.rdf.domain;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;

public class OWLAnnotationCategoryTest extends TestCase {

	@Test
	public void testUnknownAnnotationTypeName() {
		try {
			AnnotationApiModel.getByDbAnnotationTypeName("unexisting annotation type name");
			assertTrue(false);
		} catch (RuntimeException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testKnownAnnotationTypeName() {
		try {
			AnnotationApiModel.getByDbAnnotationTypeName("pathway");
			assertTrue(true);
		} catch (RuntimeException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testEnzymeRegulationParents() {
		AnnotationApiModel cat = AnnotationApiModel.getByDbAnnotationTypeName("enzyme regulation");
		assertTrue(cat.getParents().contains(AnnotationApiModel.GENERIC_INTERACTION));
		assertTrue(cat.getParents().contains(AnnotationApiModel.GENERIC_FUNCTION));
		assertTrue(cat.getParents().size()==2);
	}
	
	@Test
	public void testPathwayParents() {
		AnnotationApiModel cat = AnnotationApiModel.getByDbAnnotationTypeName("pathway");
		assertTrue(cat.getParents().contains(AnnotationApiModel.GENERIC_FUNCTION));
		assertTrue(cat.getParents().size()==1);
	}

	@Test
	public void testFunctionChildren() {
		AnnotationApiModel cat = AnnotationApiModel.GENERIC_FUNCTION;
		assertTrue(cat.getChildren().contains(AnnotationApiModel.FUNCTION_INFO));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.CATALYTIC_ACTIVITY));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.COFACTOR));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.ENZYME_REGULATION));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.PATHWAY));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.GO_BIOLOGICAL_PROCESS));
		assertTrue(cat.getChildren().contains(AnnotationApiModel.GO_MOLECULAR_FUNCTION));
		assertTrue(cat.getChildren().size()==7);
	}
	
	@Test
	public void testRoots() {
		assertTrue(AnnotationApiModel.getRoots().contains(AnnotationApiModel.NAME));
		assertTrue(AnnotationApiModel.getRoots().contains(AnnotationApiModel.GENERAL_ANNOTATION));
		assertTrue(AnnotationApiModel.getRoots().contains(AnnotationApiModel.POSITIONAL_ANNOTATION));
		assertTrue(AnnotationApiModel.getRoots().size()==3);		
	}
	
	@Test
	public void testDbAnnotationTypeNameUnicity() {
		Set<String> atns = new HashSet<String>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getDbAnnotationTypeName())) {
				System.out.println("ERROR: OWLAnnotationCategory.getDbAnnotationTypeName " + cat.getDbAnnotationTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getDbAnnotationTypeName());
			}
		}
		assertTrue(AnnotationApiModel.values().length==atns.size());
	}
	
	@Test
	public void testDbIdUnicity() {
		Set<Integer> atns = new HashSet<Integer>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getDbId())) {
				System.out.println("ERROR: OWLAnnotationCategory.getDbId " + cat.getDbId() +  " is not unique" );
			} else {
				atns.add(cat.getDbId());
			}
		}
		assertTrue(AnnotationApiModel.values().length==atns.size());
	}
	
	
	@Test
	public void testRdfTypeNameUnicity() {
		Set<String> atns = new HashSet<String>();
		for (AnnotationApiModel cat: AnnotationApiModel.values()) {
			if (atns.contains(cat.getRdfTypeName())) {
				System.out.println("ERROR: OWLAnnotationCategory.getRdfTypeName " + cat.getRdfTypeName() +  " is not unique" );
			} else {
				atns.add(cat.getRdfTypeName());
			}
		}
		assertTrue(AnnotationApiModel.values().length==atns.size());
	}
	
	/*
	 * Enum hashCode of Enum values() is based on the memory address, 
	 * so nothing to define identity of each value
	 */
	@Test
	public void testEquals() {
		Set<AnnotationApiModel> s = new HashSet<AnnotationApiModel>();
		// add each enum values twice
		for (AnnotationApiModel c: AnnotationApiModel.values()) s.add(c);
		for (AnnotationApiModel c: AnnotationApiModel.values()) s.add(c);		
		// and then check at the end that we have only one of each in the set
		int expected = AnnotationApiModel.values().length;
		System.out.println("Expected number of OWLCategories = " + expected);
		assertTrue(s.size()==expected);
	}

	@Test
	public void testTopologyAllChildren() {
		Set<AnnotationApiModel> c = AnnotationApiModel.TOPOLOGY.getAllChildren();
		assertTrue(c.contains(AnnotationApiModel.TRANSMEMBRANE_REGION));
		assertTrue(c.contains(AnnotationApiModel.INTRAMEMBRANE_REGION));
		assertTrue(c.contains(AnnotationApiModel.TOPOLOGICAL_DOMAIN));
		assertTrue(c.size()==3);
	}
	
	@Test
	public void testTopologicalDomainAllChildren() {
		Set<AnnotationApiModel> c = AnnotationApiModel.TOPOLOGICAL_DOMAIN.getAllChildren();
		assertTrue(c.size()==0);
	}
		
	@Test
	public void testNameAllChildren() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.NAME.getAllChildren();
		System.out.println("Name all children:"+cs.size());
		assertTrue(cs.contains(AnnotationApiModel.FAMILY_NAME));
		assertTrue(cs.contains(AnnotationApiModel.ENZYME_CLASSIFICATION));
		assertTrue(true);
	}
	
	
	@Test
	public void testRootsAllChildrenConsistency() {
		// set of roots
		Set<AnnotationApiModel> r = AnnotationApiModel.getRoots();
		System.out.println("Roots :"+ r.size());
		// set of children of each root
		Set<AnnotationApiModel> s1 = AnnotationApiModel.GENERAL_ANNOTATION.getAllChildren();
		System.out.println("Positional annotations :"+ s1.size());
		Set<AnnotationApiModel> s2 = AnnotationApiModel.POSITIONAL_ANNOTATION.getAllChildren();
		System.out.println("General annotations :"+ s2.size());
		Set<AnnotationApiModel> s3 = AnnotationApiModel.NAME.getAllChildren();
		System.out.println("Names :"+ s3.size());
		int count = AnnotationApiModel.values().length;
		System.out.println("Roots and children :"+ (r.size()+s1.size()+ s2.size()+s3.size()));
		System.out.println("Full count :"+count);
		// we assume that no child has more than one root parent (but it is not forbidden)
		// so the sum of each root shildren set + number of roots should be equal to enum values count
		assertTrue(r.size()+s1.size()+ s2.size()+s3.size()==count);
	}

	@Test
	public void testPositionalAnnotationAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.POSITIONAL_ANNOTATION.getAllParents();
		assertTrue(cs.size()==0);
	}
	
	@Test
	public void testActiveSiteAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.ACTIVE_SITE.getAllParents();
		assertTrue(cs.contains(AnnotationApiModel.GENERIC_SITE));
		assertTrue(cs.contains(AnnotationApiModel.POSITIONAL_ANNOTATION));
		assertTrue(cs.size()==2);
	}
	
	@Test
	public void testEnzymeRegulationAllParents() {
		Set<AnnotationApiModel> cs = AnnotationApiModel.ENZYME_REGULATION.getAllParents();
		assertTrue(cs.contains(AnnotationApiModel.GENERIC_INTERACTION));
		assertTrue(cs.contains(AnnotationApiModel.GENERIC_FUNCTION));		
		assertTrue(cs.contains(AnnotationApiModel.GENERAL_ANNOTATION));
		assertTrue(cs.size()==3);
	}
	
	public void show() {
		for (AnnotationApiModel cat : AnnotationApiModel.values()) 
			System.out.println(cat.toString());		
	}
	
}
