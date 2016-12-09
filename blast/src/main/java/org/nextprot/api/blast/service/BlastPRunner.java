package org.nextprot.api.blast.service;

import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.domain.gen.BlastResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Executes locally installed blastP program with protein sequence query
 */
public class BlastPRunner extends BlastProgram<BlastPRunner.Query, BlastResult, BlastPConfig> {

    public BlastPRunner(BlastPConfig config) {

        super("blastp", config);
        Objects.requireNonNull(config.getBinPath(), "binary path is missing");
    }

    protected BlastResult buildFromStdout(String stdout) throws IOException {

        return BlastResult.fromJson(stdout);
    }

    protected List<String> buildCommandLine(File inputFile) {

        List<String> command = new ArrayList<>();

        command.add(config.getBinPath());
        command.add("-db");
        command.add(config.getNextprotBlastDbPath());
        command.add("-query");
        command.add(inputFile.getAbsolutePath());
        command.add("-outfmt");
        command.add("15");
        if (config.getMatrix() != null) {
            command.add("-matrix");
            command.add(config.getMatrix().toString());
        }
        if (config.getEvalue() != null) {
            command.add("-evalue");
            command.add(String.valueOf(config.getEvalue()));
        }
        if (config.getGapOpen() != null) {
            command.add("-gapopen");
            command.add(String.valueOf(config.getGapOpen()));
        }
        if (config.getGapExtend() != null) {
            command.add("-gapextend");
            command.add(String.valueOf(config.getGapExtend()));
        }

        return command;
    }

    protected void writeFastaContent(PrintWriter pw, BlastPRunner.Query query) {

        config.setQueryHeader(query.getHeader());
        config.setSequenceQuery(query.getSequence());

        pw.write(new StringBuilder().append(">").append(query.getHeader()).append("\n").toString());
        pw.write(query.getSequence());
    }

    /** A fasta sequence query */
    public static class Query {

        private final String header;
        private final String sequence;

        public Query(String header, String sequence) {
            this.header = header;
            this.sequence = sequence;
        }

        public String getHeader() {
            return header;
        }

        public String getSequence() {
            return sequence;
        }
    }
}
