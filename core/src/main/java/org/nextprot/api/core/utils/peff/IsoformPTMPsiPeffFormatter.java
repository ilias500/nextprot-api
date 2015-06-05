package org.nextprot.api.core.utils.peff;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
class IsoformPTMPsiPeffFormatter extends IsoformPTMPeffFormatter {

    private static final Log Logger = LogFactory.getLog(IsoformPTMPsiPeffFormatter.class);

    // PTM-XXXX -> MOD:YYYYY
    @Deprecated
    private static Map<String, String> psiModMap = new HashMap<>();

    private static Set<AnnotationApiModel> SUPPORTED_MODELS = EnumSet.of(AnnotationApiModel.MODIFIED_RESIDUE, AnnotationApiModel.CROSS_LINK, AnnotationApiModel.LIPIDATION_SITE);

    public IsoformPTMPsiPeffFormatter() {

        super(SUPPORTED_MODELS, PeffKey.MOD_RES_PSI);
    }

    @Deprecated
    public static void addPsiModIdsToMap(Entry entry, PsiModMapper mapper) {

        List<Annotation> annotations = entry.getAnnotations();

        for (Annotation annotation : annotations) {

            if (SUPPORTED_MODELS.contains(annotation.getAPICategory())) {

                String id = annotation.getCvTermAccessionCode();
                String modId = mapper.getPsiModId(id);

                if (modId == null) {
                    Logger.warn(entry.getUniqueName()+" has a mod "+id +" w/o PSI equivalent");
                } else if (!psiModMap.containsKey(id)) {
                    psiModMap.put(id, modId);
                }
            }
        }
    }

    @Override
    public boolean isPSI() {
        return true;
    }

    @Override
    protected String getModName(Annotation annotation) {

        return annotation.getCvTermAccessionCode();
        //return (psiModMap.containsKey(annotation.getCvTermAccessionCode())) ? psiModMap.get(annotation.getCvTermAccessionCode()) : "";
    }
}