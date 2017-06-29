package org.nextprot.api.core.utils.graph;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.service.TerminologyService;

public class CvTermGrphTest extends BaseCvTermGraphTest {

    @Override
    protected BaseCvTermGraph createGraph(TerminologyCv terminologyCv, TerminologyService service) {
        
        return new CvTermGrph(terminologyCv, service);
    }
}