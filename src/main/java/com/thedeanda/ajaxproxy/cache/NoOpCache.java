package com.thedeanda.ajaxproxy.cache;

public class NoOpCache implements ProxyCache {

	@Override
	public void clearCache() {
		
	}

	@Override
	public Object get(String urlPath) {
		return null;
	}

}
