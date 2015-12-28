package com.thedeanda.ajaxproxy.cache;

public interface ProxyCache {
	public void clearCache();
	//public void add(String urlPath, byte[] responseData);
	public Object get(String urlPath);
}
