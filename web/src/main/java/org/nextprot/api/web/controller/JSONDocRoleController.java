package org.nextprot.api.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.pojo.*;
import org.jsondoc.core.util.JSONDocType;
import org.jsondoc.springmvc.controller.JSONDocController;
import org.jsondoc.springmvc.scanner.SpringJSONDocScanner;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.web.service.impl.ExportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;

@Controller
public class JSONDocRoleController extends JSONDocController {

	private final static Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);

    private JSONDoc jsonDoc;

	public JSONDocRoleController() {
		super(null, "", null);
	}

	@Autowired
	private Environment env;
	
	@Autowired
	private ReleaseInfoService releaseInfoService;
	
	
	private static ApiMethodDoc cloneMethodDocWithName(ApiMethodDoc met, String name, String additionalDescription){
		ApiMethodDoc m = new ApiMethodDoc();
		m.setQueryparameters(met.getQueryparameters());
		Set<String> produces = new HashSet<String>();
		produces.add(MediaType.APPLICATION_XML_VALUE);
		produces.add(MediaType.APPLICATION_JSON_VALUE);
		m.setProduces(produces);
		m.setConsumes(met.getConsumes());
		Set<ApiParamDoc> set = new HashSet<ApiParamDoc>();
		String[] allowedvalues = {"NX_P01308"};
		set.add(new ApiParamDoc("entry", "Exports only the " + name + " from an entry. " + additionalDescription,  new JSONDocType("string"),  "true", allowedvalues, null, null));
		m.setPathparameters(set);
		m.setPath("/entry/{entry}/" + StringUtils.camelToKebabCase(name));
		m.setVerb(ApiVerb.GET);
		
		return m;
	}

	@PostConstruct
	public void init() {
		
		List<String> packages = new ArrayList<String>();
		packages.addAll(Arrays.asList(new String[] { "org.nextprot.api.commons", 
				"org.nextprot.api.core", 
				"org.nextprot.api.rdf", 
				"org.nextprot.api.solr", 
				"org.nextprot.api.user",
				"org.nextprot.api.web" }));
		
		String version = releaseInfoService.findReleaseContents().getApiRelease();
		for(String profile : env.getActiveProfiles()){
			if(profile.equalsIgnoreCase("build")){
				packages.add("org.nextprot.api.build");
				break;
			}
		}
		
		jsonDoc = new SpringJSONDocScanner().getJSONDoc(version, "", packages);
		for(Set<ApiDoc> apiDocs: jsonDoc.getApis().values()) {
			for(ApiDoc apiDoc: apiDocs) {
				ApiMethodDoc met = null;
				if(apiDoc.getName().equals("Entry") && apiDoc.getMethods() != null && !apiDoc.getMethods().isEmpty()) {
					met = apiDoc.getMethods().iterator().next();
				}
				
				if (apiDoc.getName().equals("Entry")) {

					//adding blocks
					for (EntryBlock block: EntryBlock.values()) {
						if(!block.equals(EntryBlock.FULL_ENTRY))
							apiDoc.getMethods().add(cloneMethodDocWithName(met, block.name().toLowerCase().replaceAll("_", "-"), ""));
					}

					
					//adding subparts
					for (AnnotationApiModel model: AnnotationApiModel.values()) {
						String additionalDescription = "It locates on the hierarchy: " + model.getHierarchy();
						apiDoc.getMethods().add(cloneMethodDocWithName(met, StringUtils.camelToKebabCase(model.getApiTypeName()), additionalDescription));
					}
				}
			}
		}
	}

	@RequestMapping(value = JSONDocController.JSONDOC_DEFAULT_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public @ResponseBody JSONDoc getApi() {
		Set<String> contextRoles = NPSecurityContext.getCurrentUserRoles();
		LOGGER.info("Context roles");
		for (String role : contextRoles) {
			LOGGER.info(role);
		}

		Map<String, Set<ApiDoc>> contextApis = new TreeMap<String, Set<ApiDoc>>();
		for(Entry<String, Set<ApiDoc>> apis: jsonDoc.getApis().entrySet()) {

			// For each class annotation (ApiDoc)
			Set<ApiDoc> contextApiDocs = new TreeSet<ApiDoc>();
//				boolean devMode = false;
//				if (env != null) {
//					String[] pfs = env.getActiveProfiles();
//					if (pfs != null) {
//						for (String e : pfs) {
//							if (e.equalsIgnoreCase("dev")) {
//								devMode = true;
//								break;
//							}
//						}
//					}
//				}
			for (ApiDoc apiDoc : apis.getValue()) {

				// Check authorization at class level 
				if (apiDoc.getAuth() == null || apiDoc.getAuth().equals("ROLE_ANONYMOUS") || 
						(contextRoles != null && !Collections.disjoint(contextRoles, apiDoc.getAuth().getRoles()))) {

					// For each method annotation (ApiMethodDoc)
					Set<ApiMethodDoc> contextApiMethodDocs = new TreeSet<ApiMethodDoc>();
					for (ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
					
						// Check authorization at method level 
						if (apiMethodDoc.getAuth() == null || apiMethodDoc.getAuth().equals("ROLE_ANONYMOUS") ||
								contextRoles != null && !Collections.disjoint(contextRoles, apiMethodDoc.getAuth().getRoles())) {
							contextApiMethodDocs.add(apiMethodDoc);
						}
					}
					if (!contextApiMethodDocs.isEmpty()) {
						//Create a copy of apiDoc but with methods according to contextRoles
						ApiDoc tmpApiDoc = new ApiDoc();
						tmpApiDoc.setDescription(apiDoc.getDescription());
						tmpApiDoc.setName(apiDoc.getName());
						tmpApiDoc.setGroup(apiDoc.getGroup());
						tmpApiDoc.setMethods(contextApiMethodDocs);
						tmpApiDoc.setSupportedversions(apiDoc.getSupportedversions());
						tmpApiDoc.setAuth(apiDoc.getAuth());

						contextApiDocs.add(tmpApiDoc);
					}
				}
			}
			if (!contextApiDocs.isEmpty()) {
				contextApis.put(apis.getKey(), contextApiDocs);
				LOGGER.info("Add \"" + apis.getKey() + "\" Api to the current user");						
			}
		}
		
		JSONDoc contextJSONDoc = new JSONDoc(releaseInfoService.findReleaseContents().getApiRelease(), "");
		contextJSONDoc.setApis(contextApis);
		contextJSONDoc.setObjects(jsonDoc.getObjects());
		contextJSONDoc.setFlows(jsonDoc.getFlows());

		return contextJSONDoc;
	}
	

}
