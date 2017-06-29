package org.nextprot.api.core.utils.graph;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.graph.IntGraph;
import org.nextprot.api.core.service.TerminologyService;

import java.io.Serializable;

/**
 * A hierarchy of {@code CvTerm} ids organised in a Directed Acyclic Graph.
 */
public class CvTermGraph extends BaseCvTermGraph implements Serializable {

    public CvTermGraph(TerminologyCv terminologyCv, TerminologyService service) {

        super(terminologyCv, service, IntGraph::new);
    }
}
