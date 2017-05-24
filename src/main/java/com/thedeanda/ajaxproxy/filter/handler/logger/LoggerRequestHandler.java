package com.thedeanda.ajaxproxy.filter.handler.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.cache.LruCache;
import com.thedeanda.ajaxproxy.filter.handler.RequestHandler;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;

public class LoggerRequestHandler implements RequestHandler {
	private static final Logger log = LoggerFactory.getLogger(LoggerRequestHandler.class);

	private static final String JS_CONTENT_TYPE = "application/javascript";

	private static final String JS_RESOURCE = "/js/logger.js";

	private LruCache<String, String> cache = new LruCache<>(10);

	private String jsContents;

	private ThreadPoolExecutor executor;

	private LoggerMessageListener listener;

	public LoggerRequestHandler(LoggerMessageListener listener) throws IOException {
		this.listener = listener;
		
		loadResource();
		
		executor = new ThreadPoolExecutor(1, 6, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		executor.allowCoreThreadTimeOut(true);
		
	}

	@Override
	public boolean handleRequest(HttpServletRequest request, HttpServletResponse response,
			ProxyContainer proxyContainer, RequestListener requestListener) throws ServletException, IOException {

		String uri = request.getRequestURI();
		log.debug(uri);

		String method = request.getMethod();

		if ("POST".equals(method)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(request.getInputStream(), baos);
			byte[] inputData = baos.toByteArray();
			executor.execute(new LogParser(inputData, listener));
			handlePostResponse(uri, response);
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

	private void handlePostResponse(String uri, HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		response.flushBuffer();
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
