package com.thedeanda.ajaxproxy.filter;

import java.io.ByteArrayInputStream;
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

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyPath;
import com.thedeanda.ajaxproxy.model.ProxyPathMatcher;

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

	private Set<ProxyPathMatcher> matchers;

	public ProxyFilter(AjaxProxy ajaxProxy) {
		this.ajaxProxy = ajaxProxy;
		client = new HttpClient();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			doFilterInternal((HttpServletRequest) request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}

	private void doFilterInternal(final HttpServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		log.info("proxy filter");

		String uri = request.getRequestURI();
		log.info(uri);
		ProxyPathMatcher matcher = getMatchingMatcher(uri);
		if (matcher != null) {
			ProxyPath proxyPath = matcher.getProxyPath();
			log.warn("found matcher for new proxy method {}", matcher);
			StringBuilder sb = new StringBuilder();
			List<Header> hdrs = new LinkedList<Header>();
			@SuppressWarnings("unchecked")
			Enumeration<String> hnames = request.getHeaderNames();
			while (hnames.hasMoreElements()) {
				String hn = hnames.nextElement();
				if (!"Host".equals(hn)) {
					Header h = new BasicHeader(hn, request.getHeader(hn));
					hdrs.add(h);
					sb.append(hn + ": " + request.getHeader(hn));
				}
			}
			log.info("headers: {}", sb);

			StringBuilder proxyUrl = new StringBuilder();
			proxyUrl.append("http://" + proxyPath.getDomain());
			if (proxyPath.getPort() != 80) {
				proxyUrl.append(":" + proxyPath.getPort());
			}
			proxyUrl.append(uri);
			log.info("new proxy method to: {}", proxyUrl);

			client.makeRequest(RequestMethod.GET, proxyUrl.toString(),
					sb.toString(), null, new RequestListener() {

						@Override
						public void newRequest(UUID id, String url,
								String method) {
							// TODO Auto-generated method stub

						}

						@Override
						public void startRequest(UUID id, URL url,
								Header[] requestHeaders, byte[] data) {
							// TODO Auto-generated method stub

						}

						@Override
						public void requestComplete(UUID id, int status,
								String reason, long duation,
								Header[] responseHeaders, byte[] data) {

							try {
								ServletOutputStream os = response
										.getOutputStream();
								IOUtils.copy(new ByteArrayInputStream(data), os);
							} catch (Exception e) {

							}

						}

						@Override
						public void error(UUID id, String message, Exception e) {
							// chain.doFilter(request, response);
						}

					});
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {

	}

	private ProxyPathMatcher getMatchingMatcher(String uri) {
		ProxyPathMatcher ret = null;
		for (ProxyPathMatcher matcher : matchers) {
			if (matcher.matches(uri)) {
				ret = matcher;
				break;
			}
		}
		return ret;
	}

	public void reset() {
		List<ProxyPath> paths = ajaxProxy.getProxyPaths();
		matchers = new HashSet<ProxyPathMatcher>();
		for (ProxyPath path : paths) {
			try {
				Pattern pattern = Pattern.compile(path.getPath());
				ProxyPathMatcher matcher = new ProxyPathMatcher();
				matcher.setPattern(pattern);
				matcher.setProxyPath(path);
				matchers.add(matcher);
			} catch (Exception e) {
				log.debug("skipping: {}", path, e);
			}
		}
	}

}
