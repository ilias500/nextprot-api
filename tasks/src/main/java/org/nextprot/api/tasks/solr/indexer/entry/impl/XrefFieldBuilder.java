package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

@EntryFieldBuilder
public class XrefFieldBuilder extends FieldBuilder {

    @Override
    protected void init(Entry entry) {

        String[] extraNameCat = {"entry name", "family name", "allergen name", "reaction ID", "toxin name"};
        // Xrefs
        List<DbXref> xrefs = entry.getXrefs();
        for (DbXref xref : xrefs) {
            String acc = xref.getAccession();
            String db = xref.getDatabaseName();
            if (db.equals(NEXTPROT_SUBMISSION.getName())) continue;
            if (db.equals(HPA.getName()) && !acc.contains(ENSG.getName())) { // HPA with ENSG are for expression
                addField(Fields.ANTIBODY, acc);
            }
            if (db.equals(ENSEMBL.getName())) {
                addField(Fields.ENSEMBL, acc);
            }
            // There is an inconsistency in the way EMBL xref properties are managed: 
            // for genomic sequences EAW78410.1 -> molecule type=protein, the pid appears as an individual xref
            // and the EMBL acc is a property EAW78410.1 -> genomic sequence ID=CH471052
            // but for mrnas BC040557 -> protein sequence ID=AAH40557.1, the pid is just a property of the xref...
            if (!(db.equals(PEPTIDE_ATLAS.getName()) || db.equals(SRM_ATLAS.getName()))) { // These are indexed under the 'peptide' field

                if (db.equals(EMBL.getName())) {
                    String propvalue = xref.getPropertyValue("protein sequence ID");
                    if (propvalue != null) {
                        addField(Fields.XREFS, "EMBL:" + propvalue + ", " + propvalue);
                        addField(Fields.XREFS, "EMBL:" + acc + ", " + acc);
                    } else {
                        propvalue = xref.getPropertyValue("genomic sequence ID");
                        if (propvalue != null || !acc.contains(".")) {
                            addField(Fields.XREFS, "EMBL:" + acc + ", " + acc);
                        }
                    }
                } else {
                    addField(Fields.XREFS, db + ":" + acc + ", " + acc);
                    for (String category : extraNameCat) {
                        String extraName = xref.getPropertyValue(category);
                        if (extraName != null) { // Can be found for dbs: "InterPro", "Pfam", "PROSITE"), "TIGRFAMs", "SMART", "PRINTS", "HAMAP",
                            // "PeroxiBase", "PIRSF", "PIR", "TCDB", "CAZy", "ESTHER", UniPathway
                            addField(Fields.XREFS, db + ":" + extraName + ", " + extraName);
                            break;
                        }
                    }
                }
            }

        }

        // It is weird to have to go thru this to get the CAB antibodies, they should come with getXrefs()
        List<Annotation> annots = entry.getAnnotations();
        for (Annotation currannot : annots) {
            String category = currannot.getCategory();

            if ("pathway".equals(category)) {
                addField(Fields.XREFS, "Pathway:" + currannot.getDescription() + ", " + currannot.getDescription());
            } else if ("disease".equals(category)) { // Same remark
                DbXref parentXref = currannot.getParentXref();
                if (parentXref != null && parentXref.getDatabaseName().equals(ORPHANET.getName())) {
                    String disName = parentXref.getPropertyValue("disease");
                    addField(Fields.XREFS, "Disease:" + disName + ", " + disName);
                }
            } else if ("SmallMoleculeInteraction".equals(category)) { // Same remark
                addField(Fields.XREFS, "generic name:" + currannot.getDescription() + ", " + currannot.getDescription());
            }
        }

        // Isoform ids
        List<Isoform> isoforms = entry.getIsoforms();
        for (Isoform iso : isoforms) {
            String isoId = iso.getIsoformAccession().substring(3);
            addField(Fields.XREFS, "isoform ID:" + isoId + ", " + isoId);
        }
        // Xrefs to publications (PubMed, DOIs)
        for (Publication currpubli : entry.getPublications()) {
            List<PublicationDbXref> pubxrefs = currpubli.getDbXrefs();
            for (DbXref pubxref : pubxrefs) {
                String acc = pubxref.getAccession().trim(); // It happens to have a trailing \t (like 10.1080/13547500802063240 in NX_P14635)
                String db = pubxref.getDatabaseName();
                addField(Fields.XREFS, db + ":" + acc + ", " + acc);
            }
        }

    }

    @Override
    public Collection<Fields> getSupportedFields() {
        return Arrays.asList(Fields.XREFS, Fields.ENSEMBL, Fields.ANTIBODY);
    }

}
