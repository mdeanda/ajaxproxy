package com.thedeanda.ajaxproxy.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;

@Data
public class ProxyPathMatcher {
	private ProxyPath proxyPath;
	private Pattern pattern;

	public boolean matches(String uri) {
		Matcher matcher = pattern.matcher(uri);
		return matcher.matches();
	}
	
}
