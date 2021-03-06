package org.nextprot.api.core.service.export.format;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;

import java.util.*;

public enum EntryBlock {
	
	FULL_ENTRY(null, NextprotMediaType.XML, NextprotMediaType.TXT),
	ACCESSION(NextprotMediaType.XML, NextprotMediaType.TXT),
	OVERVIEW(NextprotMediaType.XML),
	ANNOTATION(NextprotMediaType.XML),
	PUBLICATION(NextprotMediaType.XML),
	XREF(NextprotMediaType.XML),
	IDENTIFIER(NextprotMediaType.XML),
	CHROMOSOMAL_LOCATION(NextprotMediaType.XML),
	EXPERIMENTAL_CONTEXT(NextprotMediaType.XML),
	MDATA(NextprotMediaType.XML),
	GENOMIC_MAPPING(NextprotMediaType.XML),
	ISOFORM(NextprotMediaType.XML);
	
	private List<NextprotMediaType> supportedFormats = null;

	EntryBlock(NextprotMediaType... supportedFormats){
		this.supportedFormats = Arrays.asList(supportedFormats);
	}
	
	public String getURLFormat(){
		return this.name().replaceAll("_", "-").toLowerCase();
	}
	
	public static boolean containsBlock(String s){
		String aux = s.toUpperCase().replaceAll("-", "_");
	    for (EntryBlock c : EntryBlock.values()) {
	        if (c.name().equals(aux)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	public static EntryBlock valueOfViewName(String s){
		String aux = s.toUpperCase().replaceAll("-", "_");
		return EntryBlock.valueOf(aux);
	}

	private static HashMap<String, Set<String>> formatViews = null;

	static {
		formatViews = new HashMap<>();
		for (NextprotMediaType format : NextprotMediaType.values()) {
			formatViews.put(format.name().toLowerCase(), new LinkedHashSet<String>());
			for (EntryBlock v : EntryBlock.values()) {
				if (v.supportedFormats.contains(format)) {
					formatViews.get(format.name().toLowerCase()).add(v.getURLFormat());
					if(v.equals(ANNOTATION)){
						getAnnotationHierarchy(AnnotationCategory.ROOT, formatViews.get(format.name().toLowerCase()), 0);
					}
				}
			}
		}
	}
	
	private static void getAnnotationHierarchy(AnnotationCategory a, Set<String> list, int inc) {
		if(inc > 0) {
			String name = new String(new char[inc]).replace('\0', '-') + StringUtils.camelToKebabCase(a.getApiTypeName());
			list.add(name);
		}
		
		int nextInc = inc + 1;
		for (AnnotationCategory c : a.getChildren()) {
			getAnnotationHierarchy(c, list, nextInc);
		}
	}
	

	public static Map<String, Set<String>> getFormatViews() {
		return formatViews;
	}
}
