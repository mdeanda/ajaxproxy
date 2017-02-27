package com.thedeanda.ajaxproxy.servlet;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFileServlet extends AbstractFileServlet {
	private static final Logger log = LoggerFactory.getLogger(SimpleFileServlet.class);
	private static final long serialVersionUID = -3760579412885003066L;

	private File basePath;
	/**
	 * remove prefix of request uri
	 */
	private String filterPath;

	public SimpleFileServlet(String basePath, String filterPath) {
		this.basePath = new File(basePath);
		this.filterPath = filterPath;
	}

	@Override
	public ResponseContent getFile(HttpServletRequest request) throws FileServletException {
		log.warn(request.getRequestURI());
		String requestPath = request.getRequestURI();

		File file = null;

		if (!StringUtils.isBlank(filterPath) && requestPath.startsWith(filterPath)) {
			requestPath = requestPath.substring(filterPath.length());
		}

		// TODO: prevent ../../../ paths from going outside of basePath
		file = new File(basePath, requestPath);
		log.warn("get file: " + file.getAbsolutePath());

		return new ResponseContentFile(file);
	}

}
