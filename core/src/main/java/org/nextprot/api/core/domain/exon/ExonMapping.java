package org.nextprot.api.core.domain.exon;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.utils.IsoformUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ExonMapping implements Serializable {

    private static final long serialVersionUID = 3L;

    private Map<GeneRegion, Map<String, CategorizedExon>> exons = new HashMap<>();
    private List<String> sortedExonKeys = new ArrayList<>();
    private Map<String, Map<String, Object>> isoformInfos = new HashMap<>();
    private List<String> nonAlignedIsoforms = new ArrayList<>();
    private List<Integer> startExonPositions;
    private List<Integer> stopExonPositions;

    public Map<GeneRegion, Map<String, CategorizedExon>> getExons() {
        return exons;
    }

    public void setExons(Map<GeneRegion, Map<String, CategorizedExon>> exons) {

        this.exons = exons;
        this.sortedExonKeys.addAll(new ArrayList<>(exons.keySet()).stream()
                        .sorted(Comparator.comparingInt(GeneRegion::getFirstPosition)
                                .thenComparingInt(GeneRegion::getLastPosition))
                        .map(gr -> gr.toString())
                        .collect(Collectors.toList()));

        this.startExonPositions = extractStartExonPositions();
        this.stopExonPositions = extractStopExonPositions();
    }

    private List<Integer> extractStartExonPositions() {

        return exons.values().stream()
                .map(m -> m.values())
                .flatMap(m -> m.stream())
                .filter(e -> e.getExonCategory() == ExonCategory.START)
                .map(e -> ((ExonStart) e).getStartPosition())
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Integer> extractStopExonPositions() {

        return exons.values().stream()
                .map(m -> m.values())
                .flatMap(m -> m.stream())
                .filter(e -> e.getExonCategory() == ExonCategory.STOP)
                .map(e -> ((ExonStop) e).getStopPosition())
                .sorted(Comparator.reverseOrder())
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, Map<String, Object>> getIsoformInfos() {

        return Collections.unmodifiableMap(isoformInfos);
    }

    public void setIsoformInfos(String isoformAccession, List<String> ensts, String mainName) {

        if (this.isoformInfos.containsKey(isoformAccession)) {

            throw new NextProtException("infos already exist for isoform "+isoformAccession);
        }

        this.isoformInfos.put(isoformAccession, new HashMap<>());

        Map<String, Object> infos = isoformInfos.get(isoformAccession);

        infos.put("accession", isoformAccession);
        infos.put("name", mainName);
        infos.put("main-transcript", ensts.get(0));
        if (ensts.size() > 1) {
            infos.put("other-transcripts", ensts.subList(1, ensts.size()));
        }
    }

    public List<String> getSortedExonKeys() {

        return Collections.unmodifiableList(sortedExonKeys);
    }

    public List<String> getSortedIsoformKeys() {

        return Collections.unmodifiableList(isoformInfos.keySet().stream()
                .sorted(new IsoformUtils.ByIsoformUniqueNameComparator())
                .collect(Collectors.toList()));
    }

    public List<String> getNonAlignedIsoforms() {
        return nonAlignedIsoforms;
    }

    public void setNonAlignedIsoforms(List<String> nonAlignedIsoforms) {
        this.nonAlignedIsoforms = nonAlignedIsoforms;
    }

    public List<Integer> getStartExonPositions() {
        return startExonPositions;
    }

    public List<Integer> getStopExonPositions() {
        return stopExonPositions;
    }
}
