package com.thedeanda.ajaxproxy.filter.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.cache.LruCache;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonException;

public class LoggerRequestHandler implements RequestHandler {
	private static final Logger log = LoggerFactory.getLogger(LoggerRequestHandler.class);

	private static final String JS_CONTENT_TYPE = "application/javascript";

	private static final String JS_RESOURCE = "/js/logger.js";

	private LruCache<String, String> cache = new LruCache<>(10);

	private String jsContents;

	public LoggerRequestHandler() throws IOException {
		loadResource();
	}

	@Override
	public boolean handleRequest(HttpServletRequest request, HttpServletResponse response,
			ProxyContainer proxyContainer, final RequestListener requestListener) throws ServletException, IOException {

		String uri = request.getRequestURI();
		log.debug(uri);

		String method = request.getMethod();

		if ("POST".equals(method)) {
			try {
				JsonArray array = JsonArray.parse(request.getInputStream());
				handleLogMessages(array);
			} catch (JsonException e) {
				log.warn(e.getMessage(), e);
			}
		} else {
			// ignore input and just return javascript
			handleJsResponse(uri, response);
		}

		return true;
	}

	private void handleJsResponse(String uri, HttpServletResponse response) throws IOException {
		String output = cache.get(uri);
		if (StringUtils.isBlank(output)) {
			output = jsContents.replaceAll("%PATH%", uri);
			cache.put(uri, output);
		}

		response.setContentType(JS_CONTENT_TYPE);
		response.getWriter().write(output);
		response.flushBuffer();
	}

	private void handleLogMessages(JsonArray array) {
		log.warn("array data: {}", array);
	}

	private void loadResource() throws IOException {
		try (InputStream is = getClass().getResourceAsStream(JS_RESOURCE);
				InputStreamReader isr = new InputStreamReader(is)) {
			StringWriter sw = new StringWriter();
			IOUtils.copy(isr, sw);
			jsContents = sw.toString();
		}

	}
}
