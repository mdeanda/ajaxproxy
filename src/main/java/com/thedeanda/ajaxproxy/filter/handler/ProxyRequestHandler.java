package com.thedeanda.ajaxproxy.filter.handler;

import com.thedeanda.ajaxproxy.cache.model.CachedResponse;
import com.thedeanda.ajaxproxy.config.model.proxy.HttpHeader;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProxyRequestHandler implements RequestHandler {
	private static final Logger log = LoggerFactory.getLogger(ProxyRequestHandler.class);
	private ProxyConfigRequest proxyConfig;
	private HttpClient client;

	public ProxyRequestHandler(ProxyConfigRequest proxyConfig) {
		this.proxyConfig = proxyConfig;
		client = new HttpClient();
	}

	@Override
	public boolean handleRequest(HttpServletRequest request, HttpServletResponse response, ProxyContainer proxy,
			final RequestListener requestListener) throws ServletException, IOException {
		log.debug("using new proxy filter");

		String uri = request.getRequestURI();
		log.debug(uri);
		String requestPath = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		String fullUrl = CachedResponse.getFullUrl(requestPath, queryString);

		List<HttpHeader> inputHeaders = new ArrayList<>();
		List<Header> hdrs = new LinkedList<Header>();
		@SuppressWarnings("unchecked")
		Enumeration<String> hnames = request.getHeaderNames();

		String hostHeaderString = proxyConfig.getHost().getValue(); // default header
		if (!StringUtils.isBlank(proxyConfig.getHostHeader())) {
			hostHeaderString = proxyConfig.getHostHeader();
		}

		Header hostHeader = new BasicHeader("Host", hostHeaderString);
		hdrs.add(hostHeader);
		inputHeaders.add(HttpHeader.builder().name("Host").value(hostHeaderString).build());

		while (hnames.hasMoreElements()) {
			String hn = hnames.nextElement();
			if (!isHeaderBlacklisted(hn)) {
				// TODO: see rest client frame for a whitelist
				// TODO: consider allowing header replacement via config
				Header h = new BasicHeader(hn, request.getHeader(hn));
				hdrs.add(h);
				inputHeaders.add(HttpHeader.builder().name(hn).value(request.getHeader(hn)).build());
			}
		}
		log.trace("headers: {}", inputHeaders);

		StringBuilder proxyUrl = new StringBuilder();
		proxyUrl.append(proxyConfig.getProtocol() + "://" + proxyConfig.getHost().getValue());
		if (("http".equalsIgnoreCase(proxyConfig.getProtocol()) && proxyConfig.getPort() != 80)
				|| ("https".equalsIgnoreCase(proxyConfig.getProtocol()) && proxyConfig.getPort() != 443)) {
			proxyUrl.append(":" + proxyConfig.getPort());
		}
		proxyUrl.append(uri);
		if (queryString != null) {
			proxyUrl.append("?" + queryString);
		}
		log.trace("new proxy method to: {}", proxyUrl);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(request.getInputStream(), baos);
		byte[] inputData = baos.toByteArray();

		CachedResponse cachedResponse = null;
		// TODO: remove the host/port portion
		if ("GET".equals(request.getMethod())) {
			cachedResponse = proxy.getCache().get(proxyUrl.toString());
		} else {
			proxy.getCache().clearCache();
		}
		if (cachedResponse == null || cachedResponse.getData() == null) {
			cachedResponse = makeRequest(request, response, proxyUrl.toString(), inputHeaders, inputData,
					requestListener);
			cachedResponse.setRequestPath(requestPath);
			cachedResponse.setQueryString(queryString);
			if (cachedResponse.getStatus() > 0 && cachedResponse.getStatus() < 300
					&& "GET".equals(request.getMethod())) {
				proxy.getCache().cache(cachedResponse);
			}
		} else {
			// send cached response
			sendCachedResponse(request, inputHeaders, cachedResponse, response, requestListener);
		}

		return true;
	}

	private boolean isHeaderBlacklisted(String headerName) {
		Set<String> blacklist = new HashSet<>();
		blacklist.add("host");
		blacklist.add("content-length");
		blacklist.add("connection");
		blacklist.add("transfer-encoding");
		blacklist.add("keep-alive");
		blacklist.add("trailer");
		return blacklist.contains(headerName.toLowerCase());
	}

	private CachedResponse makeRequest(HttpServletRequest request, final HttpServletResponse response, String proxyUrl,
			List<HttpHeader> inputHeaders, byte[] inputData, final RequestListener listener) {
		final CachedResponse cachedResponse = new CachedResponse();
		client.makeRequest(RequestMethod.valueOf(request.getMethod()), proxyUrl, inputHeaders, inputData,
				new RequestListener() {

					@Override
					public void newRequest(UUID id, String url, String method) {
						listener.newRequest(id, url, method);
						cachedResponse.setUrl(url);
					}

					@Override
					public void startRequest(UUID id, URL url, Header[] requestHeaders, byte[] data) {
						listener.startRequest(id, url, requestHeaders, data);
					}

					@Override
					public void requestComplete(UUID id, int status, String reason, long duration,
							Header[] responseHeaders, byte[] data) {
						cachedResponse.setData(data);
						cachedResponse.setStatus(status);
						cachedResponse.setReason(reason);
						// TODO: add cached header so its easy to tell it was
						// cached
						cachedResponse.setHeaders(responseHeaders);
						response.setStatus(status);

						try {
							// TODO: add response headers here to pass them
							// along too!
							log.debug("response headers:\n{}", (Object[]) responseHeaders);

							for (Header h : responseHeaders) {
								response.addHeader(h.getName(), h.getValue());
							}
							if (data != null) {
								ServletOutputStream os = response.getOutputStream();
								IOUtils.copy(new ByteArrayInputStream(data), os);
							}
						} catch (Exception e) {
							log.warn(e.getMessage(), e);
						}

						listener.requestComplete(id, status, reason, duration, responseHeaders, data);
					}

					@Override
					public void error(UUID id, String message, Exception ex) {
						// chain.doFilter(request, response);
						log.debug("error: id/message/ex - {}, {}, {}", id, message, ex);

						//add generic 502 error content
						cachedResponse.setStatus(502);
						send502(response);
						listener.error(id, message, ex);
					}

				});

		return cachedResponse;
	}

	private void send502(HttpServletResponse response) {
		// TODO: read Server name/version string from common place. current method is tied to ui code
		String appVersion = "AjaxProxy";
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
		response.setHeader("Server", appVersion);
		response.setHeader("Date", sdf.format(dt));
		response.setHeader("Content-Type", "text/html");


		try {
			ServletOutputStream os = response.getOutputStream();
			StringBuilder sb = new StringBuilder();
			sb.append("<html>\n");
			sb.append("<head><title>502 Bad Gateway</title></head>\n");
			sb.append("<body bgcolor=\"white\">\n");
			sb.append("<center><h1>502 Bad Gateway</h1></center>\n");
			sb.append("<hr><center>" + appVersion + "</center>\n");
			sb.append("</body>\n");
			sb.append("</html>\n");
			IOUtils.copy(new StringReader(sb.toString()), os, "UTF-8");
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	private void sendCachedResponse(HttpServletRequest request, List<HttpHeader> inputHeaders, CachedResponse cachedResponse,
			final HttpServletResponse response, final RequestListener listener) throws IOException {
		log.debug("Using cached response: {}", cachedResponse.getUrl());
		ServletOutputStream os = response.getOutputStream();
		for (Header h : cachedResponse.getHeaders()) {
			response.addHeader(h.getName(), h.getValue());
		}
		IOUtils.copy(new ByteArrayInputStream(cachedResponse.getData()), os);

		// now just notify listener so ui can function properly

		Map<String, String> hds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(inputHeaders)) {
			for (HttpHeader header : inputHeaders) {
				hds.put(header.getName(), header.getValue());
			}
		}
		Header[] requestHeaders = null;
		if (hds.size() > 0) {
			requestHeaders = new Header[hds.size()];
			int i = 0;
			for (String key : hds.keySet()) {
				Header h = new BasicHeader(key, hds.get(key));
				requestHeaders[i++] = h;
			}
		}

		UUID id = UUID.randomUUID();
		String url = cachedResponse.getUrl();
		Header[] responseHeaders = cachedResponse.getHeaders();
		listener.newRequest(id, url, "GET");
		listener.startRequest(id, new URL(url), requestHeaders, new byte[] {});
		listener.requestComplete(id, cachedResponse.getStatus(), cachedResponse.getReason(), 0, responseHeaders,
				cachedResponse.getData());
	}
}
