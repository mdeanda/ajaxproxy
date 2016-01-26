package com.thedeanda.ajaxproxy.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.cache.MemProxyCache;
import com.thedeanda.ajaxproxy.cache.NoOpCache;
import com.thedeanda.ajaxproxy.cache.model.CachedResponse;
import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;
import com.thedeanda.ajaxproxy.model.config.AjaxProxyConfig;
import com.thedeanda.ajaxproxy.model.config.ProxyConfig;

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
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest
				&& response instanceof HttpServletResponse) {
			ProxyContainer proxy = getProxyMatcher((HttpServletRequest) request);
			if (proxy != null) {
				doFilterInternal((HttpServletRequest) request,
						(HttpServletResponse) response, chain, proxy);
			} else {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private ProxyContainer getProxyMatcher(final HttpServletRequest request) {
		String uri = request.getRequestURI();
		log.trace(uri);
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
			final HttpServletResponse response, final FilterChain chain,
			ProxyContainer proxy) throws IOException, ServletException {
		log.debug("using new proxy filter");

		String uri = request.getRequestURI();
		log.debug(uri);
		String queryString = request.getQueryString();

		ProxyConfig proxyConfig = proxy.getProxyConfig();

		StringBuilder inputHeaders = new StringBuilder();
		List<Header> hdrs = new LinkedList<Header>();
		@SuppressWarnings("unchecked")
		Enumeration<String> hnames = request.getHeaderNames();

		Header hostHeader = new BasicHeader("Host", proxyConfig.getHost());
		hdrs.add(hostHeader);
		inputHeaders.append("Host: " + proxyConfig.getHost() + "\n");

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
		log.trace("headers: {}", inputHeaders);

		StringBuilder proxyUrl = new StringBuilder();
		proxyUrl.append("http://" + proxyConfig.getHost());
		if (proxyConfig.getPort() != 80) {
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
		String requestPath = request.getRequestURL().toString();
		if ("GET".equals(request.getMethod())) {
			cachedResponse = proxy.getCache().get(requestPath);
		} else {
			proxy.getCache().clearCache();
		}
		if (cachedResponse == null) {
			cachedResponse = makeRequest(request, response, proxyUrl,
					inputHeaders, inputData);
			cachedResponse.setRequestPath(requestPath);
			if (cachedResponse.getStatus() > 0
					&& "GET".equals(request.getMethod())) {
				proxy.getCache().cache(cachedResponse);
			}
		} else {
			// send cached response
			sendCachedResponse(request, inputHeaders.toString(),
					cachedResponse, response);
		}
	}

	private void sendCachedResponse(HttpServletRequest request, String headers,
			CachedResponse cachedResponse, final HttpServletResponse response)
			throws IOException {
		log.debug("Using cached response: {}", cachedResponse.getUrl());
		ServletOutputStream os = response.getOutputStream();
		for (Header h : cachedResponse.getHeaders()) {
			response.addHeader(h.getName(), h.getValue());
		}
		IOUtils.copy(new ByteArrayInputStream(cachedResponse.getData()), os);

		// now just notify listener so ui can function properly

		Map<String, String> hds = new HashMap<>();
		if (!StringUtils.isBlank(headers)) {
			String[] lines = StringUtils.split(headers, "\n");
			for (String line : lines) {
				String[] parts = StringUtils.split(line, ":", 2);
				hds.put(parts[0], parts[1]);
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
		listener.requestComplete(id, cachedResponse.getStatus(),
				cachedResponse.getReason(), 0, responseHeaders,
				cachedResponse.getData());
	}

	private CachedResponse makeRequest(HttpServletRequest request,
			final HttpServletResponse response, StringBuilder proxyUrl,
			StringBuilder inputHeaders, byte[] inputData) {
		final CachedResponse cachedResponse = new CachedResponse();
		client.makeRequest(RequestMethod.valueOf(request.getMethod()),
				proxyUrl.toString(), inputHeaders.toString(), inputData,
				new RequestListener() {

					@Override
					public void newRequest(UUID id, String url, String method) {
						listener.newRequest(id, url, method);
						cachedResponse.setUrl(url);
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
							log.debug("response headers:\n{}",
									(Object[]) responseHeaders);

							ServletOutputStream os = response.getOutputStream();
							for (Header h : responseHeaders) {
								response.addHeader(h.getName(), h.getValue());
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

		return cachedResponse;
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
		AjaxProxyConfig config = ajaxProxy.getAjaxProxyConfig();
		proxyContainers = new HashSet<ProxyContainer>();
		for (ProxyConfig proxyConfig : config.getProxyConfig()) {
			if (proxyConfig.isNewProxy()) {
				try {
					Pattern pattern = Pattern.compile(proxyConfig.getPath());
					ProxyContainer proxyContainer = new ProxyContainer();
					proxyContainer.setPattern(pattern);
					proxyContainer.setProxyConfig(proxyConfig);
					proxyContainers.add(proxyContainer);

					if (proxyConfig.isEnableCache()) {
						long cacheTime = TimeUnit.SECONDS.toMillis(proxyConfig.getCacheDuration());
						proxyContainer.setCache(new MemProxyCache(cacheTime));
					} else {
						proxyContainer.setCache(new NoOpCache());
					}
				} catch (Exception e) {
					log.debug("skipping: {}", proxyConfig, e);
				}
			}
		}
	}

}
