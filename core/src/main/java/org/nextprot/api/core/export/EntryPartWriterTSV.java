package org.nextprot.api.core.export;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.export.EntryPartExporter;
import org.nextprot.api.core.export.EntryPartWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

/**
 * Writes entry parts in tabs-separated-values
 * Delegate the type of infos to export to {@code EntryPartExporter}
 */
public class EntryPartWriterTSV extends EntryPartWriter {

    private final EntryPartExporter exporter;

    public EntryPartWriterTSV(EntryPartExporter exporter, OutputStream os) {

        super(os);
        this.exporter = exporter;
    }

    @Override
    public void writeHeader() throws IOException {

        getOutputStream().write(exporter.exportHeaders().stream()
                .map(Enum::name)
                .collect(Collectors.joining("\t")).getBytes());
        getOutputStream().write("\n".getBytes());
    }

    @Override
    public void writeRows(Entry entry) throws IOException {

        getOutputStream().write(exporter.exportRows(entry).stream()
                .map(row -> row.stream().collect(Collectors.joining("\t")))
                .collect(Collectors.joining("\n")).getBytes());
    }
}
