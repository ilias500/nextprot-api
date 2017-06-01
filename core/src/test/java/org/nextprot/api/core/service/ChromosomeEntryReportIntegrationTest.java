package org.nextprot.api.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.lucene.util.Counter;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.export.io.ChromosomeReportTXTReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


public class ChromosomeEntryReportIntegrationTest {

	@Test
	public void chromosomeEntryReportsShouldMatchFTPReports() throws Exception {

        PrintWriter pw = new PrintWriter("/tmp/ftpVSapiChromosomeEntryReports.tsv");

		pw.write(DifferenceAnalyser.reportAllChromosomes());

		pw.close();
	}

	@Ignore
	@Test
	public void calculateLongestGeneInfoStrings() throws Exception {

		List<String> allChromosomes = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
				"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y", "MT", "unknown");

		ObjectMapper mapper = new ObjectMapper();
		LongestGeneStringHandler handler = new LongestGeneStringHandler();

		for (String chromosome : allChromosomes) {

			URL url = new URL("http://build-api.nextprot.org/chromosome-report/" + chromosome + ".json");
			ChromosomeReport report = mapper.readValue(new InputStreamReader(url.openStream()), ChromosomeReport.class);

			for (EntryReport er : report.getEntryReports()) {

				handler.handleGeneName(EntryReport.getValidGeneNameValue(er.getGeneName()));
				handler.handleGeneStartPosition(er.getGeneStartPosition());
				handler.handleGeneStopPosition(er.getGeneEndPosition());
			}
		}
	}

	private static class DifferenceAnalyser {

		private final String chromosome;
		private ChromosomeReport chromosomeReportFromFTP;
		private ChromosomeReport chromosomeReportFromAPI;
		private Differences differences;

        private DifferenceAnalyser(String chromosome) throws IOException, ParseException {
			this.chromosome = chromosome;
			readReports();
			calcDifferences();
		}

		private void readReports() throws IOException, ParseException {

			ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

			URL ftpUrl = new URL("ftp://ftp.nextprot.org/pub/current_release/chr_reports/nextprot_chromosome_"+chromosome+".txt");
			chromosomeReportFromFTP = reader.read(new InputStreamReader(ftpUrl.openStream()));

			URL apiURL = new URL("http://build-api.nextprot.org/chromosome-report/export/"+chromosome);
			chromosomeReportFromAPI = reader.read(new InputStreamReader(apiURL.openStream()));
		}

		private Differences calcDifferences() {

			differences = new Differences(this);

			// 1. row count diffs
			differences.setRowNumberInApi(chromosomeReportFromAPI.getEntryReports().size());
			differences.setRowNumberInFTP(chromosomeReportFromFTP.getEntryReports().size());

			// 2. Summary diffs
			calcSummaryDiffs();

			// 3. Distinct entries
			Set<String> entriesInAPI = chromosomeReportFromAPI.getEntryReports().stream()
					.map(EntryReport::getAccession)
					.collect(Collectors.toSet());

			Set<String> entriesInFTP = chromosomeReportFromFTP.getEntryReports().stream()
					.map(EntryReport::getAccession)
					.collect(Collectors.toSet());

			differences.addAllDistinctAccsInAPI(Sets.difference(entriesInAPI, entriesInFTP));
			differences.addAllDistinctAccsInFTP(Sets.difference(entriesInFTP, entriesInAPI));

			// 4. Distinct genes
			Set<String> genesInAPI = chromosomeReportFromAPI.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.collect(Collectors.toSet());

			Set<String> genesInFTP = chromosomeReportFromFTP.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.collect(Collectors.toSet());

			differences.addAllDistinctGenesInAPI(Sets.difference(genesInAPI, genesInFTP));
			differences.addAllDisctinctGenesInFTP(Sets.difference(genesInFTP, genesInAPI));

			// 5. Gene name duplication delta
			differences.setGeneDuplicatesDelta(
			        calcDiffDuplicateGenes(
			                collectGeneDuplicates(chromosomeReportFromFTP),
                            collectGeneDuplicates(chromosomeReportFromAPI)
                    )
            );

			return differences;
		}

		private Map<String, Integer> collectGeneDuplicates(ChromosomeReport chromosomeReport) {

			Map<String, Counter> map = new HashMap<>();

			chromosomeReport.getEntryReports().stream()
					.map(EntryReport::getGeneName)
					.filter(gn -> !"-".equals(gn))
					.forEach(acc -> {
						if (!map.containsKey(acc))
							map.put(acc, Counter.newCounter());
						map.get(acc).addAndGet(1);
					});

			return map.entrySet().stream()
					.filter(e -> e.getValue().get() > 1)
					.collect(Collectors.toMap(Map.Entry::getKey, p -> (int)p.getValue().get()-1));
		}

		private void calcSummaryDiffs() {

			ChromosomeReport.Summary summaryFTP = chromosomeReportFromFTP.getSummary();
			ChromosomeReport.Summary summaryAPI = chromosomeReportFromAPI.getSummary();

			differences.setDeltaEntryCount(Math.abs(summaryAPI.getEntryCount() - summaryFTP.getEntryCount()));
			differences.setDeltaGeneCount(Math.abs(summaryAPI.getGeneCount() - summaryFTP.getGeneCount()));
		}

        private Map<String, Integer> calcDiffDuplicateGenes(Map<String, Integer> fromFTP,  Map<String, Integer> fromAPI) {

            Map<String, Integer> diffMap = new HashMap<>();

            Set<String> genesFromFTP = fromFTP.keySet();
            Set<String> genesFromAPI = fromAPI.keySet();

            for (String geneName : genesFromAPI) {

                if (!genesFromFTP.contains(geneName)) {
                    throw new IllegalStateException("gene name " + geneName + " was not found in ftp chromosome report");
                }
            }

            for (String geneName : genesFromFTP) {

                int diff = fromFTP.get(geneName) - fromAPI.getOrDefault(geneName, 0);

                if (diff > 0)
                    diffMap.put(geneName, diff);
            }

            return diffMap;
        }

		Differences getDifferences() {

			return differences;
		}

		public static String reportAllChromosomes() throws IOException, ParseException {

			StringBuilder sb = new StringBuilder();

			sb.append(Differences.getHeaders().stream().collect(Collectors.joining("\t"))).append("\n");
			for (String chromosome : ChromosomeReportService.getChromosomeNames()) {

				DifferenceAnalyser analyser = new DifferenceAnalyser(chromosome);
				sb.append(analyser.getDifferences().getValues().stream().collect(Collectors.joining("\t"))).append("\n");
			}

			return sb.toString();
		}
	}

	private static class Differences {

		private final DifferenceAnalyser analyser;

		private final Set<String> distinctEntryReportAccsInAPI = new HashSet<>();
		private final Set<String> distinctEntryReportAccsInFTP = new HashSet<>();
		private final Set<String> distinctEntryReportGenesInAPI = new HashSet<>();
		private final Set<String> distinctEntryReportGenesInFTP = new HashSet<>();
		private int rowNumberInApi;
		private int rowNumberInFTP;
		private int deltaEntryCount;
		private int deltaGeneCount;
		private Map<String, Integer> geneDuplicatesDelta;

        private Differences(DifferenceAnalyser analyser) {
			this.analyser = analyser;
		}

		public void addAllDistinctAccsInAPI(Collection<String> notInAPIAccs) {
			distinctEntryReportAccsInAPI.addAll(notInAPIAccs);
		}

		public void addAllDistinctAccsInFTP(Collection<String> notInFTPAccs) {

			distinctEntryReportAccsInFTP.addAll(notInFTPAccs);
		}

		public void addAllDistinctGenesInAPI(Collection<String> notInAPIGenes) {
			distinctEntryReportGenesInAPI.addAll(notInAPIGenes);
		}

		public void addAllDisctinctGenesInFTP(Collection<String> notInFTPGenes) {

			distinctEntryReportGenesInFTP.addAll(notInFTPGenes);
		}

		public void setGeneDuplicatesDelta(Map<String, Integer> geneDuplicatesDelta) {
			this.geneDuplicatesDelta = geneDuplicatesDelta;
		}

		public void setRowNumberInApi(int rowNumberInApi) {
			this.rowNumberInApi = rowNumberInApi;
		}

		public void setRowNumberInFTP(int rowNumberInFTP) {
			this.rowNumberInFTP = rowNumberInFTP;
		}

		public void setDeltaEntryCount(int deltaEntryCount) {
			this.deltaEntryCount = deltaEntryCount;
		}

		public void setDeltaGeneCount(int deltaGeneCount) {
			this.deltaGeneCount = deltaGeneCount;
		}

		public static List<String> getHeaders() {
			return Arrays.asList("chromosome",
					"delta row count (abs(api-ftp))", "row count (api)", "row count (ftp)",
					"delta entry count (abs(api-ftp))", "entry count (api)", "entry count (ftp)",
					"delta gene count (abs(api-ftp))", "gene count (api)", "gene count (ftp)",
					"distinct entry count (api)", "distinct entry count (ftp)", "distinct entries (ftp)",
					"distinct gene count (api)", "distinct gene count (ftp)", "distinct genes (ftp)",
                    "gene duplicates count (ftp-api)", "gene duplicates delta (ftp-api)"
			);
		}

		public List<String> getValues() {

			return Arrays.asList(analyser.chromosome,
					String.valueOf(Math.abs(rowNumberInApi-rowNumberInFTP)), String.valueOf(rowNumberInApi), String.valueOf(rowNumberInFTP),
					String.valueOf(deltaEntryCount), String.valueOf(analyser.chromosomeReportFromAPI.getSummary().getEntryCount()), String.valueOf(analyser.chromosomeReportFromFTP.getSummary().getEntryCount()),
					String.valueOf(deltaGeneCount), String.valueOf(analyser.chromosomeReportFromAPI.getSummary().getGeneCount()), String.valueOf(analyser.chromosomeReportFromFTP.getSummary().getGeneCount()),
					String.valueOf(distinctEntryReportAccsInAPI.size()), String.valueOf(distinctEntryReportAccsInFTP.size()), distinctEntryReportAccsInFTP.toString(),
					String.valueOf(distinctEntryReportGenesInAPI.size()), String.valueOf(distinctEntryReportGenesInFTP.size()), distinctEntryReportGenesInFTP.toString(),
					String.valueOf(geneDuplicatesDelta.values().stream().mapToInt(Integer::intValue).sum()), geneDuplicatesDelta.toString()
			);
		}
	}

	private static class LongestGeneStringHandler  {

		private String longestGeneNameString = "";
		private String longestGeneStartString = "";
		private String longestGeneStopPosString = "";

		public String handleGeneName(String geneName) {

			if (geneName.length() > longestGeneNameString.length()) {
				longestGeneNameString = geneName;
			}

			return geneName;
		}

		public String handleGeneStartPosition(String start) {

			if (start.length() > longestGeneStartString.length()) {
				longestGeneStartString = start;
			}

			return start;
		}

		public String handleGeneStopPosition(String stop) {

			if (stop.length() > longestGeneStopPosString.length()) {
				longestGeneStopPosString = stop;
			}

			return stop;
		}

		public String getLongestGeneNameString() {
			return longestGeneNameString;
		}

		public String getLongestGeneStartString() {
			return longestGeneStartString;
		}

		public String getLongestGeneStopPosString() {
			return longestGeneStopPosString;
		}
	}
}

