package org.nextprot.api.web.seo.controller;

import static com.hp.hpl.jena.vocabulary.RSS.url;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.RelativeUrlUtils;
import org.nextprot.api.web.seo.domain.SeoTags;
import org.nextprot.api.web.seo.service.SeoTagsService;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

@Lazy
@Controller
public class SeoController {

	private final Log Logger = LogFactory.getLog(SeoController.class);

	@Autowired
	private SeoTagsService seoTagsService;

	@Autowired
	private GitHubService gitHubService;

	@RequestMapping(value = { "/seo/tags/**" }, method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public SeoTags getSeoTags(HttpServletRequest request) {

		try {

			String fullUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
			String url = fullUrl.substring("/seo/tags".length());

			SeoTags tags = seoTagsService.getGitHubSeoTags(url);
			if (tags != null)
				return tags;

			String firstElement = RelativeUrlUtils.getPathElements(url)[0];

			if ("entry".equals(firstElement)) {
				return seoTagsService.getEntrySeoTags(url);
			}
			if ("term".equals(firstElement)) {
				return seoTagsService.getTermSeoTags(url);
			}
			if ("publication".equals(firstElement)) {
				return seoTagsService.getPublicationSeoTags(url);
			}
			if ("news".equals(firstElement)) {
				return seoTagsService.getNewsSeoTags(url);
			}
			// default behavior
			Logger.warn("No explicit SEO tags were found for this page: " + url);
			return seoTagsService.getDefaultSeoTags(url);

		} catch (Exception e) {
			throw new NextProtException("Error while search SEO tags for this page: " + url, e);
		}

	}

	@RequestMapping(value = { "/static/**" }, method = { RequestMethod.GET }, produces = { MediaType.TEXT_HTML_VALUE })
	public ModelAndView getStaticContent(HttpServletRequest request) {

		String fullUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
		String url = fullUrl.substring("/seo/tags".length());

		SeoTags tags = seoTagsService.getGitHubSeoTags(url);
		if (tags == null){
			String firstElement = RelativeUrlUtils.getPathElements(url)[0];

			if ("entry".equals(firstElement)) {
				tags = seoTagsService.getEntrySeoTags(url);
			} else if ("term".equals(firstElement)) {
				tags = seoTagsService.getTermSeoTags(url);
			} else if ("publication".equals(firstElement)) {
				tags = seoTagsService.getPublicationSeoTags(url);
			} else if ("news".equals(firstElement)) {
				tags = seoTagsService.getNewsSeoTags(url);
			}else {
				tags = seoTagsService.getDefaultSeoTags(url);
			}
		}

        String now = (new Date()).toString();

        Map<String, Object> map = new HashMap();
        map.put("now", now);
        map.put("tags", tags);
        
		return new ModelAndView("seo", "m", map);

	}

}
