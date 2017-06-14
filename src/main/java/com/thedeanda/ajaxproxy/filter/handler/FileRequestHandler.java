package com.thedeanda.ajaxproxy.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;
import com.thedeanda.ajaxproxy.model.config.ProxyConfigFile;
import com.thedeanda.ajaxproxy.servlet.SimpleFileServlet;

public class FileRequestHandler implements RequestHandler {
	private static final String GET = "GET";
	private SimpleFileServlet servlet;

	public FileRequestHandler(ProxyConfigFile config) {
		// TODO: simple file servlet may need to mangle paths
		servlet = new SimpleFileServlet(config.getBasePath(), config.getFilterPath());
	}

	@Override
	public boolean handleRequest(HttpServletRequest request, HttpServletResponse response,
			ProxyContainer proxyContainer, final RequestListener requestListener) throws ServletException, IOException {
		if (!GET.equals(request.getMethod())) {
			return false;
		}

		servlet.doGet(request, response);
		return true;
	}

}
