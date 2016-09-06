package org.nextprot.api.etl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailure;
import org.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;
import org.nextprot.commons.statements.constants.AnnotationType;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class StatementTransformationUtil {

	private static Logger LOGGER = Logger.getLogger(StatementTransformationUtil.class);

	public static Set<TargetIsoformStatementPosition> computeTargetIsoformsForProteoformAnnotation(Statement proteoformStatement, IsoformMappingService isoformMappingService,
			List<Statement> subjectsForThisProteoform, boolean isIsoSpecific, String isoSpecificAccession, List<String> isoformAccessions) {

		List<String> isoformsToBeConsidered = new ArrayList<>();

		if (isIsoSpecific) {
			isoformsToBeConsidered.add(isoSpecificAccession);
		} else {
			isoformsToBeConsidered.addAll(isoformAccessions);
		}

		Set<TargetIsoformStatementPosition> result = new TreeSet<TargetIsoformStatementPosition>();

		for (String isoformAccession : isoformsToBeConsidered) {

			String name = null;
			boolean allOk = true;

			for (Statement s : subjectsForThisProteoform) {
				Set<TargetIsoformStatementPosition> targetIsoforms = TargetIsoformSerializer.deSerializeFromJsonString(s.getValue(StatementField.TARGET_ISOFORMS));
				List<TargetIsoformStatementPosition> targetIsoformsFiltered = targetIsoforms.stream().filter(ti -> ti.getIsoformAccession().equals(isoformAccession)).collect(Collectors.toList());

				if (targetIsoformsFiltered.isEmpty()) {
					LOGGER.debug("(skip) Could not map to isoform " + isoformAccession);
					allOk = false;
					break;
				}else if (targetIsoformsFiltered.size() > 1) {
					throw new NextProtException("Something got wrong. Found more than one target isoform for same accession" + isoformAccession);
				}
				
				TargetIsoformStatementPosition tisp = targetIsoformsFiltered.iterator().next();

				if(name == null){
					name = tisp.getName();
				}else {
					name +=  (" + " + tisp.getName());
				}
				
			}
			
			if(name != null && allOk){

				if (isIsoSpecific) {
					result.add(new TargetIsoformStatementPosition(isoformAccession, IsoTargetSpecificity.SPECIFIC.name(), name));
				}else {
					result.add(new TargetIsoformStatementPosition(isoformAccession, IsoTargetSpecificity.BY_DEFAULT.name(), name));
				}
			}
		}

		// targetIsoformsForObject =
		// TargetIsoformUtils.getTargetIsoformForObjectSerialized(subject,
		// isoformNames);

		return result;
	
	}

	/*
	@Deprecated
	public static Set<TargetIsoformStatementPosition> computeTargetIsoformsForProteoformAnnotation(IsoformMappingService isoformMappingService, List<Statement> subjects, boolean isIsoSpecific,
			List<Isoform> isoforms, List<String> isoformNames) {

		// TODO check that the subjects are all the same
		Statement subject = subjects.get(0);

		String entryAccession = subject.getValue(StatementField.ENTRY_ACCESSION);

		String isoSpecificAccession = null;

		String featureName = subject.getValue(StatementField.ANNOTATION_NAME);

		if (isIsoSpecific) {

			SequenceVariant sv = null;

			try {

				sv = new SequenceVariant(featureName);
				Isoform isoSpecific = IsoformUtils.getIsoformByName(isoforms, sv.getIsoformName());
				isoSpecificAccession = isoSpecific.getIsoformAccession();

			} catch (Exception e) {
				e.printStackTrace();
				throw new NextProtException(e.getMessage());
			}

		} else {

			FeatureQueryResult featureQueryResult = isoformMappingService.propagateFeature(featureName, "variant", entryAccession);
			if (featureQueryResult.isSuccess()) {
				FeatureQuerySuccess response = ((FeatureQuerySuccess) featureQueryResult);
				response.getData().values().forEach(i -> i.getIsoSpecificFeature());
			}

		}

		// targetIsoformsForObject =
		// TargetIsoformUtils.getTargetIsoformForObjectSerialized(subject,
		// isoformNames);

		return TargetIsoformUtils.getTargetIsoformForPhenotype(subject, isoformNames, isIsoSpecific, isoSpecificAccession, "WTF");

	}

	 */
	
	static List<Statement> getPropagatedStatementsForEntry(IsoformMappingService isoformMappingService, Set<Statement> multipleSubjects, String nextprotAccession) {

		List<Statement> result = new ArrayList<>();

		for (Statement subject : multipleSubjects) {

			FeatureQueryResult featureQueryResult = null;
			featureQueryResult = isoformMappingService.propagateFeature(subject.getValue(StatementField.ANNOTATION_NAME), "variant", nextprotAccession);
			if (featureQueryResult.isSuccess()) {
				result.add(mapVariationStatementToEntry(subject, (FeatureQuerySuccess) featureQueryResult));
			} else {
				FeatureQueryFailure failure = (FeatureQueryFailure) featureQueryResult;
				String message = "Failure for " + subject.getStatementId() + " " + failure.getError().getMessage();
				LOGGER.error(message);
				System.err.println(message);
			}
		}

		if (result.size() == multipleSubjects.size()) {
			return result;
		} else
			return new ArrayList<Statement>(); // return an empty list

	}

	public static Map<String, List<Statement>> getPropagatedStatementsForIsoform(IsoformMappingService isoformMappingService, Set<Statement> multipleSubjects, String nextprotAccession,
			boolean propagate) {

		List<Statement> result = new ArrayList<>();

		for (Statement subject : multipleSubjects) {

			FeatureQueryResult featureQueryResult = null;
			if (propagate) {
				featureQueryResult = isoformMappingService.propagateFeature(subject.getValue(StatementField.ANNOTATION_NAME), "variant", nextprotAccession);
			} else {
				featureQueryResult = isoformMappingService.validateFeature(subject.getValue(StatementField.ANNOTATION_NAME), "variant", nextprotAccession);
			}

			if (featureQueryResult.isSuccess()) {

				result.addAll(mapStatementsToEachIsoform(subject, (FeatureQuerySuccess) featureQueryResult));

			} else {
				FeatureQueryFailure failure = (FeatureQueryFailure) featureQueryResult;
				System.err.println("Failure for " + subject.getStatementId() + " " + failure.getError().getMessage());
			}
		}

		// Group the subjects by isoform
		Map<String, List<Statement>> subjectsByIsoform = result.stream().collect(Collectors.groupingBy(s -> (String) s.getValue(StatementField.ISOFORM_ACCESSION)));

		// Filter only subjects that contain all original subjects (the size is
		// the same). In other words, if 2 multiples mutants can not be mapped
		// to all isoform, the statement is not valid
		return subjectsByIsoform.entrySet().stream().filter(map -> map.getValue().size() == multipleSubjects.size()).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

	}

	static List<Statement> mapStatementsToEachIsoform(Statement statement, FeatureQuerySuccess result) {

		List<Statement> statementList = new ArrayList<>();

		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {

			if (isoformFeatureResult.isMapped()) {

				Statement rs = StatementBuilder.createNew().addMap(statement).addField(StatementField.ISOFORM_ACCESSION, isoformFeatureResult.getIsoformAccession())
						.addField(StatementField.RAW_STATEMENT_ID, statement.getStatementId()) // Keep
																								// a
																								// reference
																								// to
																								// the
																								// original
																								// statement
						.addField(StatementField.LOCATION_BEGIN, String.valueOf(isoformFeatureResult.getBeginIsoformPosition()))
						.addField(StatementField.LOCATION_END, String.valueOf(isoformFeatureResult.getEndIsoformPosition()))
						.addField(StatementField.LOCATION_BEGIN_MASTER, String.valueOf(isoformFeatureResult.getBeginMasterPosition()))
						.addField(StatementField.LOCATION_END_MASTER, String.valueOf(isoformFeatureResult.getEndMasterPosition()))
						.addField(StatementField.ISOFORM_CANONICAL, String.valueOf(isoformFeatureResult.isCanonical()))
						.addField(StatementField.ANNOTATION_NAME, String.valueOf(isoformFeatureResult.getIsoSpecificFeature())).buildWithAnnotationHash(AnnotationType.ISOFORM);

				statementList.add(rs);

			}

		}

		return statementList;
	}

	/**
	 * @param variationStatement
	 *            Can be a variant or mutagenesis
	 * @param result
	 * @return
	 */
	static Statement mapVariationStatementToEntry(Statement variationStatement, FeatureQuerySuccess result) {

		String beginPositionOfCanonicalOrIsoSpec = null;
		String endPositionOfCanonicalOrIsoSpec = null;

		String masterBeginPosition = null;
		String masterEndPosition = null;

		String isoCanonical = null;

		Set<TargetIsoformStatementPosition> targetIsoforms = new TreeSet<TargetIsoformStatementPosition>();

		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {
			if (isoformFeatureResult.isMapped()) {

				targetIsoforms.add(new TargetIsoformStatementPosition(isoformFeatureResult.getIsoformAccession(), isoformFeatureResult.getBeginIsoformPosition(),
						isoformFeatureResult.getEndIsoformPosition(), IsoTargetSpecificity.BY_DEFAULT.name(), // Target
																												// by
																												// default
																												// to
																												// all
																												// variations
																												// (the
																												// subject
																												// is
																												// always
																												// propagated)
						isoformFeatureResult.getIsoSpecificFeature()));

				// Will be set in case that we don't want to propagate to
				// canonical
				if (beginPositionOfCanonicalOrIsoSpec == null) {
					beginPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getBeginIsoformPosition());
				}
				if (endPositionOfCanonicalOrIsoSpec == null) {
					endPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getEndIsoformPosition());
				}

				// If possible use canonical
				if (isoformFeatureResult.isCanonical()) {
					if (isoCanonical != null) {
						throw new NextProtException("Canonical position set already");
					}
					isoCanonical = isoformFeatureResult.getIsoformAccession();
					beginPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getBeginIsoformPosition());
					endPositionOfCanonicalOrIsoSpec = String.valueOf(isoformFeatureResult.getEndIsoformPosition());
				}

				if (masterBeginPosition == null) {
					masterBeginPosition = String.valueOf(isoformFeatureResult.getBeginMasterPosition());
				}

				if (masterEndPosition == null) {
					masterEndPosition = String.valueOf(isoformFeatureResult.getEndMasterPosition());
				}

				if (masterBeginPosition != null) {
					if (!masterBeginPosition.equals(String.valueOf(isoformFeatureResult.getBeginMasterPosition()))) {
						throw new NextProtException("Begin master position " + masterBeginPosition + " does not match " + String.valueOf(isoformFeatureResult.getBeginMasterPosition()
								+ " for different isoforms (" + result.getData().values().size() + ") for statement " + variationStatement.getStatementId()));
					}
				}

				if (masterEndPosition != null) {
					if (!masterEndPosition.equals(String.valueOf(isoformFeatureResult.getEndMasterPosition()))) {
						throw new NextProtException("End master position does not match for different isoforms" + variationStatement.getStatementId());
					}
				}

			}

		}

		Statement rs = StatementBuilder.createNew().addMap(variationStatement).addField(StatementField.RAW_STATEMENT_ID, variationStatement.getStatementId()) // Keep
																																								// a
																																								// reference
																																								// to
																																								// the
																																								// original
																																								// statement
				.addField(StatementField.LOCATION_BEGIN, beginPositionOfCanonicalOrIsoSpec).addField(StatementField.LOCATION_END, endPositionOfCanonicalOrIsoSpec)
				.addField(StatementField.LOCATION_BEGIN_MASTER, masterBeginPosition).addField(StatementField.LOCATION_END_MASTER, masterEndPosition)
				.addField(StatementField.ISOFORM_ACCESSION, variationStatement.getValue(StatementField.ENTRY_ACCESSION)).addField(StatementField.ISOFORM_CANONICAL, isoCanonical)
				.addField(StatementField.TARGET_ISOFORMS, TargetIsoformSerializer.serializeToJsonString(targetIsoforms)).buildWithAnnotationHash(AnnotationType.ENTRY);

		return rs;
	}

}
