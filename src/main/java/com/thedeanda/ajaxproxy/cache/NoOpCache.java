package com.thedeanda.ajaxproxy.cache;

import com.thedeanda.ajaxproxy.cache.model.CachedResponse;

public class NoOpCache implements ProxyCache {

	@Override
	public void clearCache() {
	}

	@Override
	public void cache(CachedResponse response) {
	}

	@Override
	public CachedResponse get(String urlPath) {
		return null;
	}

}
