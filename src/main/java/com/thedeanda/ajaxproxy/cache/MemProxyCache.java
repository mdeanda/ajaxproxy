package com.thedeanda.ajaxproxy.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
	private static final long CACHE_TIME = TimeUnit.MINUTES.toMillis(3);

	private Object lock = new Object();

	public MemProxyCache() {
		super(capacity, 1.1f, true);
		log.debug("new cache");
	}

	private void cleanupExpired(CachedResponse response) {
		synchronized (lock) {
			remove(response.getRequestPath());
			// TODO: also look for other expired items
		}
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
		long expiredTs = System.currentTimeMillis() - CACHE_TIME;
		CachedResponse response = null;
		synchronized (lock) {
			response = super.get(urlPath);
		}
		if (response != null && response.getTimestamp() < expiredTs) {
			cleanupExpired(response);
			response = null;
		}
		return response;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<String, CachedResponse> eldest) {
		return size() > capacity;
	}
}
