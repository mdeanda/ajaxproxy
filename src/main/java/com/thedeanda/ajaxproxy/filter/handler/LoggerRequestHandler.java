package com.thedeanda.ajaxproxy.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;

public class LoggerRequestHandler implements RequestHandler {
	private static final Logger log = LoggerFactory.getLogger(LoggerRequestHandler.class);

	@Override
	public boolean handleRequest(HttpServletRequest request, HttpServletResponse response,
			ProxyContainer proxyContainer, final RequestListener requestListener) throws ServletException, IOException {
		// TODO Auto-generated method stub

		return false;
	}

}
