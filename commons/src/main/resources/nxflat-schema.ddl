CREATE TABLE nxflat.ENTRY_MAPPED_STATEMENTS (
	STATEMENT_ID VARCHAR(4000),
	NEXTPROT_ACCESSION VARCHAR(4000),
	ENTRY_ACCESSION VARCHAR(4000),
	GENE_NAME VARCHAR(4000),
	LOCATION_BEGIN_MASTER VARCHAR(4000),
	LOCATION_END_MASTER VARCHAR(4000),
	LOCATION_BEGIN VARCHAR(4000),
	LOCATION_END VARCHAR(4000),
	ISOFORM_ACCESSION VARCHAR(4000),
	SUBJECT_STATEMENT_IDS VARCHAR(4000),
	SUBJECT_ANNOTATION_IDS VARCHAR(4000),
	ANNOTATION_SUBJECT_SPECIES VARCHAR(4000),
	ANNOTATION_OBJECT_SPECIES VARCHAR(4000),
	ANNOTATION_CATEGORY VARCHAR(4000),
	ANNOT_DESCRIPTION VARCHAR(4000),
	ISOFORM_CANONICAL VARCHAR(4000),
	TARGET_ISOFORMS VARCHAR(4000),
	ANNOTATION_ID VARCHAR(4000),
	ANNOTATION_NAME VARCHAR(4000),
	IS_NEGATIVE VARCHAR(4000),
	EVIDENCE_QUALITY VARCHAR(4000),
	EVIDENCE_INTENSITY VARCHAR(4000),
	EVIDENCE_NOTE VARCHAR(4000),
	EVIDENCE_STATEMENT_REF VARCHAR(4000),
	EVIDENCE_CODE VARCHAR(4000),
	EVIDENCE_PROPERTIES VARCHAR(4000),
	ANNOT_CV_TERM_TERMINOLOGY VARCHAR(4000),
	ANNOT_CV_TERM_ACCESSION VARCHAR(4000),
	ANNOT_CV_TERM_NAME VARCHAR(4000),
	VARIANT_ORIGINAL_AMINO_ACID VARCHAR(4000),
	VARIANT_VARIATION_AMINO_ACID VARCHAR(4000),
	BIOLOGICAL_OBJECT_TYPE VARCHAR(4000),
	BIOLOGICAL_OBJECT_ACCESSION VARCHAR(4000),
	BIOLOGICAL_OBJECT_DATABASE VARCHAR(4000),
	BIOLOGICAL_OBJECT_NAME VARCHAR(4000),
	OBJECT_STATEMENT_IDS VARCHAR(4000),
	OBJECT_ANNOTATION_IDS VARCHAR(4000),
	OBJECT_ANNOT_ISO_UNAMES VARCHAR(4000),
	OBJECT_ANNOT_ENTRY_UNAMES VARCHAR(4000),
	SOURCE VARCHAR(4000),
	ANNOT_SOURCE_ACCESSION VARCHAR(4000),
	EXP_CONTEXT_ECO_DETECT_METHOD VARCHAR(4000),
	EXP_CONTEXT_ECO_MUTATION VARCHAR(4000),
	EXP_CONTEXT_ECO_ISS VARCHAR(4000),
	REFERENCE_DATABASE VARCHAR(4000),
	REFERENCE_ACCESSION VARCHAR(4000),
	ASSIGNED_BY VARCHAR(4000),
	ASSIGMENT_METHOD VARCHAR(4000),
	RESOURCE_TYPE VARCHAR(4000),
	RAW_STATEMENT_ID VARCHAR(4000)
);
CREATE INDEX ENTRY_MAPP_ENTRY_AC_IDX ON nxflat.ENTRY_MAPPED_STATEMENTS ( ENTRY_ACCESSION );
CREATE INDEX ENTRY_MAPP_ANNOT_ID_IDX ON nxflat.ENTRY_MAPPED_STATEMENTS ( ANNOTATION_ID );


CREATE TABLE nxflat.RAW_STATEMENTS (
	STATEMENT_ID VARCHAR(4000),
	NEXTPROT_ACCESSION VARCHAR(4000),
	ENTRY_ACCESSION VARCHAR(4000),
	GENE_NAME VARCHAR(4000),
	LOCATION_BEGIN_MASTER VARCHAR(4000),
	LOCATION_END_MASTER VARCHAR(4000),
	LOCATION_BEGIN VARCHAR(4000),
	LOCATION_END VARCHAR(4000),
	ISOFORM_ACCESSION VARCHAR(4000),
	SUBJECT_STATEMENT_IDS VARCHAR(4000),
	SUBJECT_ANNOTATION_IDS VARCHAR(4000),
	ANNOTATION_SUBJECT_SPECIES VARCHAR(4000),
	ANNOTATION_OBJECT_SPECIES VARCHAR(4000),
	ANNOTATION_CATEGORY VARCHAR(4000),
	ANNOT_DESCRIPTION VARCHAR(4000),
	ISOFORM_CANONICAL VARCHAR(4000),
	TARGET_ISOFORMS VARCHAR(4000),
	ANNOTATION_ID VARCHAR(4000),
	ANNOTATION_NAME VARCHAR(4000),
	IS_NEGATIVE VARCHAR(4000),
	EVIDENCE_QUALITY VARCHAR(4000),
	EVIDENCE_INTENSITY VARCHAR(4000),
	EVIDENCE_NOTE VARCHAR(4000),
	EVIDENCE_STATEMENT_REF VARCHAR(4000),
	EVIDENCE_CODE VARCHAR(4000),
	EVIDENCE_PROPERTIES VARCHAR(4000),
	ANNOT_CV_TERM_TERMINOLOGY VARCHAR(4000),
	ANNOT_CV_TERM_ACCESSION VARCHAR(4000),
	ANNOT_CV_TERM_NAME VARCHAR(4000),
	VARIANT_ORIGINAL_AMINO_ACID VARCHAR(4000),
	VARIANT_VARIATION_AMINO_ACID VARCHAR(4000),
	BIOLOGICAL_OBJECT_TYPE VARCHAR(4000),
	BIOLOGICAL_OBJECT_ACCESSION VARCHAR(4000),
	BIOLOGICAL_OBJECT_DATABASE VARCHAR(4000),
	BIOLOGICAL_OBJECT_NAME VARCHAR(4000),
	OBJECT_STATEMENT_IDS VARCHAR(4000),
	OBJECT_ANNOTATION_IDS VARCHAR(4000),
	OBJECT_ANNOT_ISO_UNAMES VARCHAR(4000),
	OBJECT_ANNOT_ENTRY_UNAMES VARCHAR(4000),
	SOURCE VARCHAR(4000),
	ANNOT_SOURCE_ACCESSION VARCHAR(4000),
	EXP_CONTEXT_ECO_DETECT_METHOD VARCHAR(4000),
	EXP_CONTEXT_ECO_MUTATION VARCHAR(4000),
	EXP_CONTEXT_ECO_ISS VARCHAR(4000),
	REFERENCE_DATABASE VARCHAR(4000),
	REFERENCE_ACCESSION VARCHAR(4000),
	ASSIGNED_BY VARCHAR(4000),
	ASSIGMENT_METHOD VARCHAR(4000),
	RESOURCE_TYPE VARCHAR(4000),
	RAW_STATEMENT_ID VARCHAR(4000)
);
CREATE INDEX RAW_STATEM_ENTRY_AC_IDX ON nxflat.RAW_STATEMENTS ( ENTRY_ACCESSION );
CREATE INDEX RAW_STATEM_ANNOT_ID_IDX ON nxflat.RAW_STATEMENTS ( ANNOTATION_ID );


