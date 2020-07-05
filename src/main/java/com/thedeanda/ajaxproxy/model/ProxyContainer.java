package com.thedeanda.ajaxproxy.model;

import com.thedeanda.ajaxproxy.cache.ProxyCache;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.filter.handler.RequestHandler;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
