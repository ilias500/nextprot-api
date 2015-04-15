package org.nextprot.api.core.service.export.format;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.utils.StringUtils;

public enum NPViews{
	
	FULL_ENTRY(null, NPFileFormat.XML, NPFileFormat.TXT),
	ACCESSION(NPFileFormat.XML, NPFileFormat.TXT),
	OVERVIEW(NPFileFormat.XML),
	ANNOTATION(NPFileFormat.XML),
	PUBLICATION(NPFileFormat.XML),
	XREF(NPFileFormat.XML),
	IDENTIFIER(NPFileFormat.XML),
	CHROMOSOMAL_LOCATION(NPFileFormat.XML),
	GENOMIC_MAPPING(NPFileFormat.XML),
	INTERACTION(NPFileFormat.XML),
	ISOFORM(NPFileFormat.XML),
	ANTIBODY(NPFileFormat.XML),
	PEPTIDE(NPFileFormat.XML),
	SRM_PEPTIDE_MAPPING(NPFileFormat.XML);
	
	
	private List<NPFileFormat> supportedFormats = null;

	NPViews(NPFileFormat ... supportedFormats){
		this.supportedFormats = Arrays.asList(supportedFormats);
	}
	
	public String getURLFormat(){
		return this.name().replaceAll("_", "-").toLowerCase();
	}
	
	public static NPViews valueOfViewName(String s){
		return NPViews.valueOf(s.toUpperCase().replaceAll("_", "-"));
	}

	private static HashMap<String, Set<String>> formatViews = null;

	static {
		formatViews = new HashMap<String, Set<String>>();
		for (NPFileFormat format : NPFileFormat.values()) {
			formatViews.put(format.name().toLowerCase(), new LinkedHashSet<String>());
			for (NPViews v : NPViews.values()) {
				if (v.supportedFormats.contains(format)) {
					formatViews.get(format.name().toLowerCase()).add(v.getURLFormat());
					if(v.equals(ANNOTATION)){
						getAnnotationHierarchy(AnnotationApiModel.ROOT, formatViews.get(format.name().toLowerCase()), 0);
					}
				}
			}
		}
	}
	
	private static void getAnnotationHierarchy(AnnotationApiModel a, Set<String> list, int inc) {
		if(inc > 0) {
			String name = new String(new char[inc]).replace('\0', '-') + StringUtils.decamelizeAndReplaceByHyphen(a.getApiTypeName());
			System.out.println(name);
			list.add(name);
		}
		
		int nextInc = inc + 1;
		for (AnnotationApiModel c : a.getChildren()) {
			getAnnotationHierarchy(c, list, nextInc);
		}
	}
	

	public static Map<String, Set<String>> getFormatViews() {
		return formatViews;
	}
}
