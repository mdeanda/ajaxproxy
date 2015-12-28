package com.thedeanda.ajaxproxy.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.cache.NoOpCache;
import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;
import com.thedeanda.ajaxproxy.model.ProxyPath;

/**
 * new method of proxying requests that does not use jetty's transparent proxy
 * filter as it has a few issues
 * 
 * @author mdeanda
 * 
 */
public class ProxyFilter implements Filter {
	private static final Logger log = LoggerFactory
			.getLogger(ProxyFilter.class);

	private FilterConfig filterConfig;

	private AjaxProxy ajaxProxy;

	private HttpClient client;

	private Set<ProxyContainer> proxyContainers;

	private RequestListener listener;

	public ProxyFilter(AjaxProxy ajaxProxy) {
		this.ajaxProxy = ajaxProxy;
		client = new HttpClient();
		this.listener = ajaxProxy.getRequestListener();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			ProxyContainer proxy = getProxyMatcher((HttpServletRequest) request);
			if (proxy != null) {
				doFilterInternal((HttpServletRequest) request, response, chain,
						proxy);
			} else {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private ProxyContainer getProxyMatcher(final HttpServletRequest request) {
		String uri = request.getRequestURI();
		log.debug(uri);
		ProxyContainer matcher = getProxyForPath(uri);
		if (matcher != null) {
			return matcher;
		}
		return null;
	}

	private boolean isHeaderBlacklisted(String headerName) {
		Set<String> blacklist = new HashSet<>();
		blacklist.add("host");
		blacklist.add("content-length");
		return blacklist.contains(headerName.toLowerCase());
	}

	private void doFilterInternal(final HttpServletRequest request,
			final ServletResponse response, final FilterChain chain,
			ProxyContainer proxy) throws IOException, ServletException {
		log.debug("using new proxy filter");

		String uri = request.getRequestURI();
		log.debug(uri);
		String queryString = request.getQueryString();

		ProxyPath proxyPath = proxy.getProxyPath();

		StringBuilder inputHeaders = new StringBuilder();
		List<Header> hdrs = new LinkedList<Header>();
		@SuppressWarnings("unchecked")
		Enumeration<String> hnames = request.getHeaderNames();

		ProxyPath path = proxy.getProxyPath();
		Header hostHeader = new BasicHeader("Host", path.getDomain()/*
																	 * + ":" +
																	 * path
																	 * .getPort
																	 * ()
																	 */);
		hdrs.add(hostHeader);
		inputHeaders.append("Host: " + path.getDomain()/*
														 * + ":" +
														 * path.getPort()
														 */
				+ "\n");

		while (hnames.hasMoreElements()) {
			String hn = hnames.nextElement();
			if (!isHeaderBlacklisted(hn)) {
				// TODO: see rest client frame for a whitelist
				// TODO: consider allowing header replacement via config
				Header h = new BasicHeader(hn, request.getHeader(hn));
				hdrs.add(h);
				inputHeaders.append(hn + ": " + request.getHeader(hn) + "\n");
			}
		}
		log.info("headers: {}", inputHeaders);

		StringBuilder proxyUrl = new StringBuilder();
		proxyUrl.append("http://" + proxyPath.getDomain());
		if (proxyPath.getPort() != 80) {
			proxyUrl.append(":" + proxyPath.getPort());
		}
		proxyUrl.append(uri);
		if (queryString != null) {
			proxyUrl.append("?" + queryString);
		}
		log.info("new proxy method to: {}", proxyUrl);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(request.getInputStream(), baos);
		byte[] inputData = baos.toByteArray();

		Object cachedResponse = null;
		if ("GET".equals(request.getMethod())) {
			// proxy.getCache().get(urlPath);
		} else {
			proxy.getCache().clearCache();
		}
		if (cachedResponse == null) {
			makeRequest(request, response, proxyUrl, inputHeaders, inputData);
		} else {
			// send cached response
		}
	}

	private void makeRequest(HttpServletRequest request,
			final ServletResponse response, StringBuilder proxyUrl,
			StringBuilder inputHeaders, byte[] inputData) {
		client.makeRequest(RequestMethod.valueOf(request.getMethod()),
				proxyUrl.toString(), inputHeaders.toString(), inputData,
				new RequestListener() {

					@Override
					public void newRequest(UUID id, String url, String method) {
						listener.newRequest(id, url, method);
					}

					@Override
					public void startRequest(UUID id, URL url,
							Header[] requestHeaders, byte[] data) {
						listener.startRequest(id, url, requestHeaders, data);
					}

					@Override
					public void requestComplete(UUID id, int status,
							String reason, long duration,
							Header[] responseHeaders, byte[] data) {

						try {
							// TODO: add response headers here to pass them
							// along too!
							log.warn("response headers:\n{}", responseHeaders);

							ServletOutputStream os = response.getOutputStream();
							if (response instanceof HttpServletResponse) {
								HttpServletResponse httpResponse = (HttpServletResponse) response;
								for (Header h : responseHeaders) {
									httpResponse.addHeader(h.getName(), h.getValue());
								}
							}
							IOUtils.copy(new ByteArrayInputStream(data), os);
						} catch (Exception e) {

						}

						listener.requestComplete(id, status, reason, duration,
								responseHeaders, data);
					}

					@Override
					public void error(UUID id, String message, Exception e) {
						// chain.doFilter(request, response);
						log.debug("error: id/message/ex - {}, {}, {}", id,
								message, e);
						listener.error(id, message, e);
					}

				});
	}

	@Override
	public void destroy() {

	}

	private ProxyContainer getProxyForPath(String path) {
		ProxyContainer ret = null;
		for (ProxyContainer matcher : proxyContainers) {
			if (matcher.matches(path)) {
				log.debug("found a match!");
				ret = matcher;
				break;
			}
		}
		return ret;
	}

	public void reset() {
		List<ProxyPath> paths = ajaxProxy.getProxyPaths();
		proxyContainers = new HashSet<ProxyContainer>();
		for (ProxyPath path : paths) {
			if (path.isNewProxy()) {
				try {
					Pattern pattern = Pattern.compile(path.getPath());
					ProxyContainer proxyContainer = new ProxyContainer();
					proxyContainer.setPattern(pattern);
					proxyContainer.setProxyPath(path);
					proxyContainers.add(proxyContainer);
					// TODO: add option to use a real cache
					proxyContainer.setCache(new NoOpCache());
				} catch (Exception e) {
					log.debug("skipping: {}", path, e);
				}
			}
		}
	}

}
