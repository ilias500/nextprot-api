package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.export.format.NextprotMediaType;

import javax.servlet.http.HttpServletResponse;

public interface ChromosomeReportService {

	/**
	 * Report chromosome entries
	 * @param chromosome the chromosome to get report
	 * @return a pojo containing the report
	 */
	ChromosomeReport reportChromosome(String chromosome);

	/**
	 * Export the list of gene/neXtProt entries informations found on the given chromosome
	 * @param chromosome the chromosome to get report
	 * @param nextprotMediaType the export file format
	 * @param response the http response
	 */
	void exportChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, HttpServletResponse response);
}
