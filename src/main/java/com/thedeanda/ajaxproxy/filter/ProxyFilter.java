package com.thedeanda.ajaxproxy.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.cache.MemProxyCache;
import com.thedeanda.ajaxproxy.cache.NoOpCache;
import com.thedeanda.ajaxproxy.filter.handler.FileRequestHandler;
import com.thedeanda.ajaxproxy.filter.handler.LoggerRequestHandler;
import com.thedeanda.ajaxproxy.filter.handler.ProxyRequestHandler;
import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyContainer;
import com.thedeanda.ajaxproxy.model.config.AjaxProxyConfig;
import com.thedeanda.ajaxproxy.model.config.ProxyConfig;
import com.thedeanda.ajaxproxy.model.config.ProxyConfigFile;
import com.thedeanda.ajaxproxy.model.config.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.model.config.ProxyConfigRequest;

/**
 * new method of proxying requests that does not use jetty's transparent proxy
 * filter as it has a few issues
 * 
 * @author mdeanda
 * 
 */
public class ProxyFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(ProxyFilter.class);

	private AjaxProxy ajaxProxy;

	private Set<ProxyContainer> proxyContainers;

	private RequestListener listener;

	public ProxyFilter(AjaxProxy ajaxProxy) {
		this.ajaxProxy = ajaxProxy;
		this.listener = ajaxProxy.getRequestListener();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		boolean doChain = true;
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			ProxyContainer proxy = getProxy((HttpServletRequest) request);
			if (proxy != null) {
				if (proxy.getRequestHandler() != null) {
					doChain = !proxy.getRequestHandler().handleRequest((HttpServletRequest) request,
							(HttpServletResponse) response, proxy, listener);
				}
			}
		}

		if (doChain) {
			chain.doFilter(request, response);
		}
	}

	private ProxyContainer getProxy(final HttpServletRequest request) {
		String uri = request.getRequestURI();
		log.trace(uri);
		ProxyContainer proxyContainer = getProxyForPath(uri);
		if (proxyContainer != null) {
			return proxyContainer;
		}
		return null;
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
			loadProxyConfigRequest(proxyConfig);
		}
	}

	private void loadProxyConfigRequest(ProxyConfig proxyConfig) {
		try {
			Pattern pattern = Pattern.compile(proxyConfig.getPath());
			ProxyContainer proxyContainer = new ProxyContainer();
			proxyContainer.setPattern(pattern);
			proxyContainer.setProxyConfig(proxyConfig);
			proxyContainers.add(proxyContainer);

			// TODO: this is ugly, fix it
			if (proxyConfig instanceof ProxyConfigFile) {
				proxyContainer.setRequestHandler(new FileRequestHandler((ProxyConfigFile) proxyConfig));
			} else if (proxyConfig instanceof ProxyConfigLogger) {
				proxyContainer.setRequestHandler(new LoggerRequestHandler());
			} else if (proxyConfig instanceof ProxyConfigRequest) {
				proxyContainer.setRequestHandler(new ProxyRequestHandler((ProxyConfigRequest) proxyConfig));
			}

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
