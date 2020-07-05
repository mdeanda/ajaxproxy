package com.thedeanda.ajaxproxy.cache;

import com.thedeanda.ajaxproxy.cache.model.CachedResponse;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * in memory implementation of cache, this should be replaced by a file/db based
 * implementation to save memory
 */
public class MemProxyCache extends LinkedHashMap<String, CachedResponse> implements ProxyCache {
	private static final long serialVersionUID = -5854542957026651145L;
	private static final Logger log = LoggerFactory.getLogger(MemProxyCache.class);
	private static final int capacity = 100;

	private Object lock = new Object();
	private long cacheTimeInMillis;

	public MemProxyCache(long cacheTimeInMillis) {
		super(capacity, 1.1f, true);
		this.cacheTimeInMillis = cacheTimeInMillis;
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
	
	private String getCacheKey(CachedResponse response) {
		return response.getUrl();
	}

	@Override
	public void cache(CachedResponse response) {
		Header[] origHeaders = response.getHeaders();
		Header[] headers = new Header[origHeaders.length + 1];
		headers[0] = new BasicHeader("X-AjaxProxy", "cached");
		for (int i = 0; i < origHeaders.length; i++) {
			headers[i + 1] = origHeaders[i];
		}
		response.setHeaders(headers);
		synchronized (lock) {
			put(getCacheKey(response), response);
		}
	}

	@Override
	public CachedResponse get(String urlPath) {
		long expiredTs = System.currentTimeMillis() - cacheTimeInMillis;
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
