package org.nextprot.api.core.service.export.format;

import org.nextprot.api.commons.exception.NextProtException;

import javax.servlet.http.HttpServletRequest;

public enum NPFileFormat {

	TXT("txt", "text/plain", null, null),
	XLS("xls", "application/vnd.ms-excel", null, null),
	XML("xml", "application/xml", "<entry-list>", "</entry-list>"),
	JSON("json", "application/json", null, null), 
	TURTLE("ttl", "text/turtle", null, null), 
	TSV("tsv", "text/tab-separated-values", null, null),
	FASTA("fasta", "text/fasta", null, null),
	PEFF("peff", "text/peff", null, null)
	;

	private String header;
	private String footer;
	private String extension;
	private String contentType;

	NPFileFormat(String extension, String contentType, String header, String footer) {
		this.extension = extension;
		this.contentType = contentType;
		this.header = header;
		this.footer = footer;
	}

	public String getExtension() {
		return extension;
	}

	public String getContentType() {
		return contentType;
	}

	public String getHeader() {
		return header;
	}

	public String getFooter() {
		return footer;
	}

    public static NPFileFormat valueOf(HttpServletRequest request) {

        String uri = request.getRequestURI();
        if (uri.toLowerCase().endsWith(".ttl")) {
            return NPFileFormat.TURTLE;
        } else if (uri.toLowerCase().endsWith(".xml")) {
            return NPFileFormat.XML;
        } else if (uri.toLowerCase().endsWith(".json")) {
            return NPFileFormat.JSON;
        } else if (uri.toLowerCase().endsWith(".txt")) {
            return NPFileFormat.TXT;
		} else if (uri.toLowerCase().endsWith(".xls")) {
			return NPFileFormat.XLS;
		} else if (uri.toLowerCase().endsWith(".fasta")) {
			return NPFileFormat.FASTA;
        } else if (uri.toLowerCase().endsWith(".peff")) {
			return NPFileFormat.PEFF;
		} else
            throw new NextProtException("Format not recognized");
    }
}
