package com.thedeanda.ajaxproxy.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

public class ProxyFilter implements Filter {
	private static final Logger log = LoggerFactory
			.getLogger(ProxyFilter.class);

	private FilterConfig filterConfig;

	private AjaxProxy ajaxProxy;

	private HttpClient client;

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

		if ("/test".equals(uri)) {
			StringBuilder sb = new StringBuilder();
			List<Header> hdrs = new LinkedList<Header>();
			@SuppressWarnings("unchecked")
			Enumeration<String> hnames = request.getHeaderNames();
			while (hnames.hasMoreElements()) {
				String hn = hnames.nextElement();
				Header h = new BasicHeader(hn, request.getHeader(hn));
				hdrs.add(h);
				sb.append(hn + ": " + request.getHeader(hn));
			}

			client.makeRequest(RequestMethod.GET,
					"http://www.xmlfiles.com/examples/simple.xml", sb.toString(), null,
					new RequestListener() {

						@Override
						public void newRequest(UUID id, String url, String method) {
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

}
