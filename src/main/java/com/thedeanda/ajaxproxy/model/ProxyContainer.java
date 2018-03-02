package com.thedeanda.ajaxproxy.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thedeanda.ajaxproxy.cache.ProxyCache;
import com.thedeanda.ajaxproxy.filter.handler.RequestHandler;
import com.thedeanda.ajaxproxy.model.config.ProxyConfig;

import lombok.Data;

@Data
public class ProxyContainer {
	private ProxyCache cache;
	private ProxyConfig proxyConfig;
	private Pattern pattern;
	private RequestHandler requestHandler;

	public boolean matches(String uri) {
		Matcher matcher = pattern.matcher(uri);
		return matcher.matches();
	}

}
