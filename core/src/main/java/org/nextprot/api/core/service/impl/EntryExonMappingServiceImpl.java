package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.exon.CategorizedExon;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.EntryExonMappingService;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IsoformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntryExonMappingServiceImpl implements EntryExonMappingService {

	@Autowired
	private GenomicMappingService genomicMappingService;

	@Autowired
    private IsoformService isoformService;

	@Override
	public ExonMapping findExonMappingGeneXIsoformXShorterENST(String entryName) {

        List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);

		ExonMapping mapping = new ExonMapping();

		Optional<GenomicMapping> ogm = genomicMappingService.findGenomicMappingsByEntryName(entryName).stream()
                .filter(genomicMapping -> genomicMapping.isChosenForAlignment())
                .findFirst();

		if (ogm.isPresent()) {

            GenomicMapping gm = ogm.get();
            final Map<GeneRegion, Map<String, CategorizedExon>> categorizedExons = new HashMap<>();

			gm.getIsoformGeneMappings().stream()
                    .filter(igm -> !igm.getTranscriptGeneMappings().isEmpty())
					.peek(igm ->
                        mapping.setIsoformInfos(igm.getIsoformAccession(),
                                igm.getTranscriptGeneMappings().stream()
                                        .map(tgm -> tgm.getDatabaseAccession())
                                        .collect(Collectors.toList()),
                                igm.getIsoformMainName(),
                                igm.getQuality())
					)
					.map(igm -> igm.getTranscriptGeneMappings().get(0).getExons())
					.flatMap(e -> e.stream())
					.forEach(exon -> {
                        GeneRegion gr = (gm.isLowQualityMappings() && !exon.isStopExon()) ? exon.getCodingGeneRegion() : exon.getGeneRegion();

                        categorizedExons.computeIfAbsent(gr, k -> new HashMap<>())
								.put(exon.getIsoformName(), exon);
					});

			mapping.setExons(categorizedExons);
            mapping.setNonMappedIsoforms(gm.getNonMappingIsoforms());
            mapping.setLowQualityMappings(gm.isLowQualityMappings());
        }
		else {
            mapping.setNonMappedIsoforms(isoforms.stream()
                .map(isoform -> isoform.getIsoformAccession())
                .collect(Collectors.toList()));
        }

        mapping.calcSortedMappedIsoformKeys(getCanonicalIsoform(isoforms)
                .orElseThrow(() -> new NextProtException("could not find canonical isoform accession for entry "+ entryName))
                .getIsoformAccession());

        return mapping;
	}

	private Optional<Isoform> getCanonicalIsoform(List<Isoform> isoforms) {

	    return isoforms.stream()
                .filter(isoform -> isoform.isCanonicalIsoform())
                .findFirst();
    }
}
