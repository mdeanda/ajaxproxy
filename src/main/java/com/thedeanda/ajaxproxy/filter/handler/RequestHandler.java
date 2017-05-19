package com.thedeanda.ajaxproxy.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;

public interface RequestHandler {
	public boolean handleRequest(final HttpServletRequest request, final HttpServletResponse response,
			final ProxyContainer proxyContainer, final RequestListener requestListener)
			throws ServletException, IOException;
}
