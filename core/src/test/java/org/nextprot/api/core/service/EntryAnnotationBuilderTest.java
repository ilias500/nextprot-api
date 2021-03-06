package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.commons.constants.QualityQualifier;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformSet;

import java.util.Arrays;
import java.util.List;

public class EntryAnnotationBuilderTest extends AnnotationBuilderBastUnitTest {

    @Test
    public void shouldFindCorrectPublicationId() {

        Statement sb1 = StatementBuilder.createNew()
                .addField(StatementField.REFERENCE_DATABASE, "PubMed")
                .addField(StatementField.REFERENCE_ACCESSION, "123").build();

        StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService, dbXrefService);
        ab.findPublicationId(sb1);
    }

    @Test(expected = NextProtException.class)
    public void shouldThrowAnExceptionIfInModeStrictAndPublicationIsNotFound() {

        Statement sb1 = StatementBuilder.createNew()
                .addField(StatementField.REFERENCE_DATABASE, "PubMed")
                .addField(StatementField.REFERENCE_ACCESSION, "000").build();
        StatementAnnotationBuilder ab = StatementEntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService, dbXrefService);
        ab.findPublicationId(sb1);
    }

    @Override
    protected StatementAnnotationBuilder newAnnotationBuilder() {
        return StatementEntryAnnotationBuilder.newBuilder(terminologyService, publicationService, mainNamesService, dbXrefService);
    }

    @Test
    public void shouldReturnOneSingleAnnotationIfTheInfoIsTheSameAndItIsComingFromDifferentSources() {

        Statement sb1 = StatementBuilder.createNew().
                addCompulsaryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
                .addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
                .addField(StatementField.REFERENCE_DATABASE, "PubMed")
                .addField(StatementField.REFERENCE_ACCESSION, "123")
                .addField(StatementField.RESOURCE_TYPE, "publication")
                .addTargetIsoformsField(new TargetIsoformSet())
                .addField(StatementField.EVIDENCE_CODE, "ECO:00001")
                .addField(StatementField.ASSIGNED_BY, "TUTU")
                .addSourceInfo("CAVA-VP0920190912", "BioEditor").buildWithAnnotationHash();

        Statement sb2 = StatementBuilder.createNew().
                addCompulsaryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
                .addField(StatementField.REFERENCE_DATABASE, "PubMed")
                .addField(StatementField.REFERENCE_ACCESSION, "123")
                .addField(StatementField.RESOURCE_TYPE, "publication")
                .addTargetIsoformsField(new TargetIsoformSet())
                .addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
                .addField(StatementField.EVIDENCE_CODE, "ECO:00001")
                .addField(StatementField.ASSIGNED_BY, "TOTO")
                .addSourceInfo("HPA2222", "HPA").buildWithAnnotationHash();


        List<Statement> statements = Arrays.asList(sb1, sb2);

        Annotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308", statements);

        Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
        Assert.assertEquals(annotation.getEvidences().size(), 2);
        Assert.assertEquals(annotation.getEvidences().get(0).getEvidenceCodeName(), "eco-name-1");

    }


    @Test(expected = NextProtException.class)
    public void shouldReturnAnExceptionIf2AnnotationsAreExpectedInsteadOfOne() {

        Statement sb1 = StatementBuilder.createNew().
                addCompulsaryFields("NX_P01308", "NX_P01308", "go-cellular-component", QualityQualifier.GOLD)
                .addField(StatementField.REFERENCE_DATABASE, "PubMed")
                .addField(StatementField.REFERENCE_ACCESSION, "123")
                .addField(StatementField.RESOURCE_TYPE, "publication")
                .addTargetIsoformsField(new TargetIsoformSet())
                .buildWithAnnotationHash();

        Statement sb2 = StatementBuilder.createNew().
                addCompulsaryFields("NX_P99999", "NX_P99999", "go-cellular-component", QualityQualifier.GOLD)
                .addField(StatementField.REFERENCE_DATABASE, "PubMed")
                .addField(StatementField.REFERENCE_ACCESSION, "123")
                .addTargetIsoformsField(new TargetIsoformSet())
                .buildWithAnnotationHash();

        List<Statement> statements = Arrays.asList(sb1, sb2);

        newAnnotationBuilder().buildAnnotation("NX_P01308", statements);

    }


    @Test
    public void shouldReturnCorrectEcoName() {

        Statement sb1 = StatementBuilder.createNew().
                addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component", QualityQualifier.GOLD)
                .addField(StatementField.EVIDENCE_CODE, "ECO:00001")
                .addField(StatementField.REFERENCE_DATABASE, "PubMed")
                .addField(StatementField.REFERENCE_ACCESSION, "123")
                .addField(StatementField.RESOURCE_TYPE, "publication")
                .addTargetIsoformsField(new TargetIsoformSet())
                .buildWithAnnotationHash();

        List<Statement> statements = Arrays.asList(sb1);

        Annotation annotation = newAnnotationBuilder().buildAnnotation("NX_P01308-1", statements);

        Assert.assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
        Assert.assertEquals(annotation.getEvidences().size(), 1);
        Assert.assertEquals("eco-name-1", annotation.getEvidences().get(0).getEvidenceCodeName());

    }


}
