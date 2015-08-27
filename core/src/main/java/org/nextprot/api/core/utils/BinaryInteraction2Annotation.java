package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.Interactant;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;


public class BinaryInteraction2Annotation {

	public static Annotation transform(Interaction inter, String entryName, List<Isoform> isoforms) {
		
		// - - - - - - - - - - - - - - - - - - - - 
		// annotation core object
		// - - - - - - - - - - - - - - - - - - - - 
		Long annotId = inter.getId() ;
		
		Annotation annot = new Annotation();
		annot.setAnnotationId(annotId);
		annot.setCategory(AnnotationApiModel.BINARY_INTERACTION.getDbAnnotationTypeName());
		annot.setCvTermAccessionCode(null);
		annot.setCvTermName(null);
		annot.setDescription(null);
		annot.setParentXref(null);
		annot.setQualityQualifier(inter.getQuality());
		annot.setSynonym(null);
		annot.setUniqueName("AN"+ entryName.substring(3) + "_BI_" + annotId);
		annot.setVariant(null);
		
		// - - - - - - - - - - - - - - - - - - - - 
		// annotation evidences
		// - - - - - - - - - - - - - - - - - - - - 
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		AnnotationEvidence evi = new AnnotationEvidence();
		evi.setAnnotationId(annot.getAnnotationId());
		evi.setAssignedBy(inter.getEvidenceDatasource());
		// N/A: values = curated|computed: applicable to evidences having publications as resource
		evi.setAssignmentMethod(null); 							
		// TODO: uncomment next 2 lines as soon as partnership_resource_assoc has link to eco codes and not more to 
		// evi.setEvidenceCodeAC(inter.getEvidenceCodeAC());		
		// evi.setEvidenceCodeName(inter.getEvidenceCodeName());
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// see https://issues.isb-sib.ch/browse/NEXTPROT-921
		evi.setEvidenceCodeAC("ECO:0000353");
		evi.setEvidenceCodeName("Physical interaction evidence used in manual assertion");
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		evi.setEvidenceId(inter.getEvidenceId());
		evi.setNegativeEvidence(false); 
		evi.setExperimentalContextId(null); 
		evi.setPublicationMD5(null);										
		// see https://issues.isb-sib.ch/browse/NEXTPROT-921
		evi.setQualifierType("IPI");								 		
		evi.setQualityQualifier(inter.getEvidenceQuality());
		evi.setResourceAccession(inter.getEvidenceXrefAC());
		evi.setResourceAssociationType("evidence");			
		evi.setResourceDb(inter.getEvidenceXrefDB());
		evi.setResourceDescription(null);									
		evi.setResourceId(inter.getEvidenceResourceId());
		evi.setResourceType("database");									
		
		// - - - - - - - - - - - - - - - - - - - - 
		// evidence property: number of experiments
		// - - - - - - - - - - - - - - - - - - - - 
		AnnotationEvidenceProperty evp = new AnnotationEvidenceProperty();
		evp.setEvidenceId(evp.getEvidenceId());
		evp.setPropertyName("numberOfExperiments");
		evp.setPropertyValue(""+inter.getNumberOfExperiments());
		List<AnnotationEvidenceProperty> evProps = new ArrayList<>();
		evProps.add(evp);
		evi.setProperties(evProps); 
		
		evidences.add(evi);
		annot.setEvidences(evidences);

		// - - - - - - - - - - - - - - - - - - - - 
		// annotation properties
		// - - - - - - - - - - - - - - - - - - - - 
		// annotation property: interactant
		// - - - - - - - - - - - - - - - - - - - - 
		List<AnnotationProperty> anProps = new ArrayList<AnnotationProperty>();
		AnnotationProperty p1 = new AnnotationProperty();
		Interactant interactant=BinaryInteraction2Annotation.getInteractant(inter);
		p1.setAccession(interactant.getAccession());
		p1.setAnnotationId(annotId);
		p1.setName(AnnotationProperty.NAME_INTERACTANT);
		if (interactant.isNextprot()) {
			p1.setValueType(interactant.isIsoform() ? AnnotationProperty.VALUE_TYPE_ISO_AC : AnnotationProperty.VALUE_TYPE_ENTRY_AC);
			p1.setValue(interactant.getNextprotAccession());
		} else {
			p1.setValueType(AnnotationProperty.VALUE_TYPE_RIF);
			p1.setValue(""+interactant.getXrefId());			
		}
		anProps.add(p1);
		// - - - - - - - - - - - - - - - - - - - - 
		// annotation property: self interaction
		// - - - - - - - - - - - - - - - - - - - - 
		AnnotationProperty p3 = new AnnotationProperty();
		p3.setAnnotationId(annotId);
		p3.setName("selfInteraction");
		p3.setValue(""+inter.isSelfInteraction());
		anProps.add(p3);
		annot.setProperties(anProps);
		
		// - - - - - - - - - - - - - - - - - - - - 
		// annotation isoform specificity
		// - - - - - - - - - - - - - - - - - - - - 
		List<AnnotationIsoformSpecificity> isospecs = new ArrayList<AnnotationIsoformSpecificity>(); 
		for (Isoform iso: isoforms) {
			AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();
			spec.setAnnotationId(annotId);
			spec.setIsoformName(iso.getUniqueName());
			boolean isSpecific = inter.isInteractionSpecificForIsoform(iso.getUniqueName());
			spec.setSpecificity(isSpecific ? "SPECIFIC" : "BY DEFAULT");
			isospecs.add(spec);
		}
		annot.setTargetingIsoforms(isospecs);
		
		
		return annot;
	}
	
	/**
	 * Binary interactions have 1 or 2 interactants.
	 * When there is a single interactant, we are in a self interaction (protein interacting with another instance of itself), retrieve it
	 * When there are 2 interactants, retrieve the one which is not the annotated protein but its partner in the interaction
	 * @param inter
	 * @return
	 */
	public static Interactant getInteractant(Interaction inter) {
		Interactant interactant=null;
		if (inter.isSelfInteraction()) {
			interactant = inter.getInteractants().get(0);
		} else {
			for (Interactant it: inter.getInteractants()) {
				if (! it.isEntryPoint()) {
					interactant=it;
					break;
				}
			}			
		}
		return interactant; // should never be null
	}
	
	
	
}
