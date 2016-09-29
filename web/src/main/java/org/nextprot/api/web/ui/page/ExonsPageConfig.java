package org.nextprot.api.web.ui.page;

import java.util.ArrayList;
import java.util.Arrays;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

public class ExonsPageConfig extends SimplePageConfig {

	private static final ExonsPageConfig INSTANCE = new ExonsPageConfig();

	public static ExonsPageConfig getInstance() { return INSTANCE; }
	
	private ExonsPageConfig() {
		
		annotations = new ArrayList<>();

		features = new ArrayList<>();
		
		xrefs = new ArrayList<>();
		
	}

	@Override
	public boolean filterOutAnnotation(Annotation a) {
		return true;
	}

	@Override
	public boolean filterOutXref(DbXref x) {
		return true;
	}
	
}
