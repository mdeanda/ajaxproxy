package com.thedeanda.ajaxproxy.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.cache.model.CachedResponse;

/**
 * in memory implementation of cache, this should be replaced by a file/db based
 * implementation to save memory
 */
public class MemProxyCache extends LinkedHashMap<String, CachedResponse>
		implements ProxyCache {
	private static final long serialVersionUID = -5854542957026651145L;
	private static final Logger log = LoggerFactory
			.getLogger(MemProxyCache.class);
	private static final int capacity = 100;

	private Object lock = new Object();

	public MemProxyCache() {
		super(capacity, 1.1f, true);
	}

	@Override
	public void clearCache() {
		synchronized (lock) {
			clear();
		}
	}

	@Override
	public void cache(CachedResponse response) {
		synchronized (lock) {
			put(response.getRequestPath(), response);
		}
	}

	@Override
	public CachedResponse get(String urlPath) {
		synchronized (lock) {
			return super.get(urlPath);
		}
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<String, CachedResponse> eldest) {
		return size() > capacity;
	}
}
