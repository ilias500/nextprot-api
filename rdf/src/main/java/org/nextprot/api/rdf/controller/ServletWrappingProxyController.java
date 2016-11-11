package org.nextprot.api.rdf.controller;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ServletWrappingController;

public abstract class ServletWrappingProxyController extends ServletWrappingController implements InitializingBean {

	private String pathToStrip;

	public void setPathToStrip(String pathToStrip) {
		this.pathToStrip = pathToStrip;
	}

	abstract String getRequestMapping();

	abstract String getURLToBeProxied();

	protected Boolean getLogged() {
		return true;
	}
	
	protected String getServletName() {
		return getRequestMapping().replace("/", "");
	}

	public void afterPropertiesSet() throws Exception {

		setPathToStrip(getRequestMapping());
		super.setServletClass(org.mitre.dsmiley.httpproxy.ProxyServlet.class);
		super.setServletName(getServletName());

		Properties properties = new Properties();
		properties.put("targetUri", getURLToBeProxied());
		properties.put("log", getLogged().toString());

		super.setInitParameters(properties);
		super.afterPropertiesSet();

	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.err.println("FOUND");
		final HttpServletRequest wrapper = new HttpServletRequestWrapperInternal(this.pathToStrip, request);

		ModelAndView v = super.handleRequestInternal(wrapper, response);
		System.err.println("DONE");

		return v;
	}

	private static class HttpServletRequestWrapperInternal extends HttpServletRequestWrapper {

		private String pathToStrip;

		public HttpServletRequestWrapperInternal(String pathToStrip, HttpServletRequest request) {
			super(request);
			this.pathToStrip = pathToStrip;
		}

		@Override
		public String getPathInfo() {
			String path = super.getPathInfo();
			if (path.startsWith(pathToStrip)) {
				final int length = pathToStrip.length();
				path = path.substring(length);
			}
			return path;
		}
	}
}
