package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.PublicationDao;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.commons.utils.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class PublicationDaoImpl implements PublicationDao {

	private final static DateFormatter DATE_FORMATTER = new DateFormatter();

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;

	public List<Long> findSortedPublicationIdsByMasterId(Long masterId) {
		Map<String, Object> params = new HashMap<>();
		params.put("identifierId", masterId);
		params.put("publicationTypes", Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80));

		List<Long> ids = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("publication-sorted-for-master"), params, Long.class);
		return ids;
	}

	public List<Publication> findSortedPublicationsByMasterId(Long masterId) {
		Map<String, Object> params = new HashMap<>();
		params.put("identifierId", masterId);
		params.put("publicationTypes", Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80));

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-sorted-for-master"), params, new PublicationRowMapper());
	}

	private static class PublicationRowMapper implements ParameterizedRowMapper<Publication> {

		public Publication mapRow(ResultSet resultSet, int row) throws SQLException {

			// Need to use a mapper, but it is not so bad if we don't want to use reflection since the database may use different names
			Publication publication = new Publication();

			String pubType = resultSet.getString("pub_type");

			int cvDatePrecisionId = resultSet.getInt("cv_date_precision_id");

			if (cvDatePrecisionId != 1) {

				Date date = resultSet.getDate("publication_date");

				publication.setPublicationDate(date);
				publication.setTextDate(DATE_FORMATTER.format(date, cvDatePrecisionId));
			}

			publication.setId(resultSet.getLong("resource_id"));
			publication.setMD5(resultSet.getString("md5"));
			publication.setAbstractText(resultSet.getString("abstract_text"));
			if (!"BOOK".equals(pubType)) {
				publication.setVolume(resultSet.getString("volume"));
			} else {
				publication.setVolume("");
			}
			publication.setIssue(resultSet.getString("issue"));
			publication.setFirstPage(resultSet.getString("first_page"));
			publication.setLastPage(resultSet.getString("last_page"));
			
			// add publication details
			publication.setIsLargeScale(resultSet.getLong("is_largescale")>0);
			publication.setIsCurated(resultSet.getLong("is_curated")>0);
			publication.setIsComputed(resultSet.getLong("is_computed")>0);

			if (pubType.equals("ONLINE PUBLICATION")) {
				// In case it is a online publication
				publication.setPublicationType("ONLINE_PUBLICATION");
				publication.setTitle(resultSet.getString("volume"));
			} else if (pubType.equals("SUBMISSION")) {
				publication.setPublicationType(pubType);
				String title = resultSet.getString("title");
				publication.setTitle(title);

				String subDB = "Submitted to " + resultSet.getString("submission_database");
				if (subDB != null) {
					if (!title.startsWith(subDB)) { // add the submission database is necessary 
						//publication.setTitle(subDB + title);
						publication.setSubmission(subDB);
					}
				} 

			} else {
				publication.setPublicationType(pubType);
				publication.setTitle(resultSet.getString("title"));
			}

			String title = publication.getTitle();
			if (title.startsWith("["))
				title = title.substring(1);
			if (title.endsWith("]"))
				title = title.substring(0, title.length() - 1);

			publication.setTitle(title.replace("[", "(").replace("]", ")"));

			return publication;
		}
	}

	private static class LongRowMapper implements ParameterizedRowMapper<Long> {
		public Long mapRow(ResultSet resultSet, int row) throws SQLException {
			return resultSet.getLong("pub_id");
		}
	}
	
	public Publication findPublicationById(long id) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("resourceId", id);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("publication-by-resourceid"), namedParameters, new PublicationRowMapper());
	}

	public List<Publication> findPublicationByTitle(String title) {
		SqlParameterSource namedParameters = new MapSqlParameterSource("title", title);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-by-resourceid"), namedParameters, new PublicationRowMapper());
	}
	

	@Override
	public Publication findPublicationByMD5(String md5) {

		// Spring advantages: No need to open / close connection or to worry about result set...
		// We can use named parameters which are less error prone
		SqlParameterSource namedParameters = new MapSqlParameterSource("md5", md5);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForObject(sqlDictionary.getSQLQuery("publication-by-md5"), namedParameters, new PublicationRowMapper());
	}

	@Override
	public List<Long> findAllPublicationsIds() {
		//String sql= "select pubs.resource_id pub_id\n" + 
		//		"           from nextprot.publications pubs\n" + 
		//		"     inner join  nextprot.cv_publication_types pubtypes on ( pubs.cv_publication_type_id = pubtypes.cv_id)";
		SqlParameterSource namedParameters = new MapSqlParameterSource();
		//return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new LongRowMapper());	
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("publication-allids"), namedParameters, new LongRowMapper());	
	}

}
