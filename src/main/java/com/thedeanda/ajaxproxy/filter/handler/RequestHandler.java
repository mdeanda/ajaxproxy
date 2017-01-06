package com.thedeanda.ajaxproxy.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {
	public boolean handleRequest(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException;
}
