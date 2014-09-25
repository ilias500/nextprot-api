package org.nextprot.api.core.dao.impl;
import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.dao.impl.spring.BatchNamedParameterJdbcTemplate;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Component;


@Component
public class AnnotationDAOImpl implements AnnotationDAO {

	@Autowired
	private DataSourceServiceLocator dsLocator;

	private static class AnnotationRowMapper implements ParameterizedRowMapper<Annotation> {

		public Annotation mapRow(ResultSet resultSet, int row) throws SQLException {

			Annotation annotation = new Annotation();
			annotation.setAnnotationId(resultSet.getLong("annotation_id"));
			annotation.setCategory(resultSet.getString("category"));
			annotation.setDescription(resultSet.getString("description"));
			annotation.setQualityQualifier(resultSet.getString("quality_qualifier"));
			annotation.setCvTermName(resultSet.getString("cv_term_name"));
			annotation.setCvTermAccessionCode(resultSet.getString("cv_term_accession"));
			annotation.setSynonym(resultSet.getString("synonym"));
			annotation.setUniqueName(resultSet.getString("annotation_unique_name"));

			// Set the variant if it exists
			if ((resultSet.getString("original_sequence") != null) || (resultSet.getString("variant_sequence") != null)) {
				annotation.setVariant(new AnnotationVariant(
						resultSet.getString("original_sequence"), 
						resultSet.getString("variant_sequence"), 
						annotation.getDescription()));
			}

			return annotation;
		}
	};

	public List<Annotation> findAnnotationsByEntryName(String entryName) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("annotations-by-entry-name"), namedParameters, new AnnotationRowMapper());

	}

	// Annotation Isoforms /////////////////////////////////////////////////////////////////////////////

	private static class AnnotationIsoformRowMapper implements ParameterizedRowMapper<AnnotationIsoformSpecificity> {

		public AnnotationIsoformSpecificity mapRow(ResultSet resultSet, int row) throws SQLException {

			AnnotationIsoformSpecificity annotation = new AnnotationIsoformSpecificity();
			annotation.setAnnotationId(resultSet.getLong("annotation_id"));
			annotation.setFirstPosition(resultSet.getInt("first_pos"));
			annotation.setLastPosition(resultSet.getInt("last_pos"));
			annotation.setIsoformName(resultSet.getString("unique_name"));
			annotation.setSpecificity(resultSet.getString("iso_specificity"));
			return annotation;
		}
	};

	public List<AnnotationIsoformSpecificity> findAnnotationIsoformsByAnnotationIds(List<Long> annotationIds) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", annotationIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("annotation-isoforms-by-annotation-ids"), namedParameters, new AnnotationIsoformRowMapper());

	}

	// Annotation Evidences /////////////////////////////////////////////////////////////////////////////

	@Override
	public List<AnnotationEvidence> findAnnotationEvidencesByAnnotationIds(List<Long> annotationIds) {

		return new BatchNamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("annotation-evidences-by-annotation-ids"), "ids", annotationIds, new ParameterizedRowMapper<AnnotationEvidence>() {

			public AnnotationEvidence mapRow(ResultSet resultSet, int row) throws SQLException {

					
				AnnotationEvidence evidence = new AnnotationEvidence();
				evidence.setNegativeEvidence(resultSet.getBoolean("is_negative_evidence"));
				evidence.setAnnotationId(resultSet.getLong("annotation_id"));
				evidence.setResourceId(resultSet.getLong("resource_id"));
				evidence.setQualifierType(resultSet.getString("qualifier_type"));
				evidence.setResourceAssociationType(resultSet.getString("resource_assoc_type"));
				evidence.setResourceType(resultSet.getString("resource_type"));
				evidence.setResourceAccession(resultSet.getString("resource_accession"));
				evidence.setResourceDb(resultSet.getString("resource_db"));
				evidence.setResourceDescription(resultSet.getString("resource_desc"));
				evidence.setPublicationMD5(resultSet.getString("publication_md5"));
				evidence.setQualityQualifier(resultSet.getString("quality_qualifier"));
				evidence.setEvidenceId(resultSet.getLong("evidence_id"));
				evidence.setAssignedBy(resultSet.getString("evidence_assigned_by"));
				evidence.setExperimentalContextId(resultSet.getLong("experimental_context_id"));
				evidence.setAssignmentMethod(resultSet.getString("assignment_method"));
				return evidence;
				
			}
		});

	}
	
	
	
	@Override
	public List<AnnotationEvidenceProperty> findAnnotationEvidencePropertiesByEvidenceIds(List<Long> evidenceIds) {
		
		return new BatchNamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("annotation-evidence-properties-by-evidence-ids"), "ids", evidenceIds, new ParameterizedRowMapper<AnnotationEvidenceProperty>() {

			public AnnotationEvidenceProperty mapRow(ResultSet resultSet, int row) throws SQLException {

				AnnotationEvidenceProperty evidenceProperty = new AnnotationEvidenceProperty();
				evidenceProperty.setEvidenceId(resultSet.getLong("evidence_id"));
				//String pname = resultSet.getString("property_name");
				evidenceProperty.setPropertyName(resultSet.getString("property_name"));
				evidenceProperty.setPropertyValue(resultSet.getString("property_value"));
				return evidenceProperty;
				
			}
		});

	}


	
	// Annotation Properties /////////////////////////////////////////////////////////////////////////////

	@Override
	public List<AnnotationProperty> findAnnotationPropertiesByAnnotationIds(List<Long> annotationIds) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("ids", annotationIds);

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("annotation-properties-by-annotation-ids"), namedParameters, new ParameterizedRowMapper<AnnotationProperty>() {

			public AnnotationProperty mapRow(ResultSet resultSet, int row) throws SQLException {
			
				AnnotationProperty property = new AnnotationProperty();
				property.setAnnotationId(resultSet.getLong("annotation_id"));
				property.setName(resultSet.getString("property_name"));
				property.setValue(resultSet.getString("property_value"));
				property.setAccession(resultSet.getString("accession"));
				return property;
			}
		});

	}
}
