package com.thedeanda.ajaxproxy.cache;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.Repo;
import com.thedeanda.ajaxproxy.cache.model.CachedResponse;

public class DbCache extends Repo<CachedResponse> implements ProxyCache {
	private static final Logger log = LoggerFactory.getLogger(DbCache.class);

	public DbCache(File file) {
		super(file, CachedResponse.class);
	}

	public DbCache(File file, boolean serverMode) {
		super(file, CachedResponse.class, serverMode);
	}

	@Override
	public void clearCache() {
		try {
			deleteAll();
		} catch (SQLException e) {
			log.warn(e.getMessage(), e);
		}
	}

	@Override
	public void cache(CachedResponse response) {
		// TODO: find existing response by path+?+querystring
		int id = save(response);
		response.setId(id);
	}

	@Override
	public CachedResponse get(String urlPath) {
		Map<String, Object> matchObj = new HashMap<>();
		matchObj.put("url", urlPath);

		List<CachedResponse> items = getMatching(matchObj);
		CachedResponse response = null;
		if (items != null && !items.isEmpty()) {
			response = items.get(0);
		}
		return response;
	}

}
