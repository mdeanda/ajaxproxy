package com.thedeanda.ajaxproxy.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thedeanda.ajaxproxy.cache.ProxyCache;

public class ProxyContainer {
	private ProxyPath proxyPath;
	private ProxyCache cache;

	public ProxyCache getCache() {
		return cache;
	}

	public void setCache(ProxyCache cache) {
		this.cache = cache;
	}

	public ProxyPath getProxyPath() {
		return proxyPath;
	}

	public void setProxyPath(ProxyPath proxyPath) {
		this.proxyPath = proxyPath;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	private Pattern pattern;

	public boolean matches(String uri) {
		Matcher matcher = pattern.matcher(uri);
		return matcher.matches();
	}

}
