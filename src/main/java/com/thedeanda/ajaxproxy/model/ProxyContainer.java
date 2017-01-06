package com.thedeanda.ajaxproxy.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thedeanda.ajaxproxy.cache.ProxyCache;
import com.thedeanda.ajaxproxy.filter.handler.RequestHandler;
import com.thedeanda.ajaxproxy.model.config.ProxyConfig;

public class ProxyContainer {
	private ProxyCache cache;
	private ProxyConfig proxyConfig;
	private Pattern pattern;
	private RequestHandler requestHandler;

	public ProxyConfig getProxyConfig() {
		return proxyConfig;
	}

	public void setProxyConfig(ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	public ProxyCache getCache() {
		return cache;
	}

	public void setCache(ProxyCache cache) {
		this.cache = cache;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public boolean matches(String uri) {
		Matcher matcher = pattern.matcher(uri);
		return matcher.matches();
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

}
