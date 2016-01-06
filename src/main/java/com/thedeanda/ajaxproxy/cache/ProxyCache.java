package com.thedeanda.ajaxproxy.cache;

import com.thedeanda.ajaxproxy.cache.model.CachedResponse;

public interface ProxyCache {
	public void clearCache();
	public void cache(CachedResponse response);
	public CachedResponse get(String urlPath);
}
