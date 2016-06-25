package com.thedeanda.ajaxproxy.cache;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import com.thedeanda.ajaxproxy.Repo;
import com.thedeanda.ajaxproxy.cache.model.CachedResponse;

public class DbCache extends Repo<CachedResponse> implements ProxyCache {

	public DbCache(File file) {
		super(file, CachedResponse.class);
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
	}

	@Override
	public void cache(CachedResponse response) {
		int id = save(response);
		response.setId(id);
	}

	@Override
	public CachedResponse get(String urlPath) {
		CachedResponse matchObj = new CachedResponse();
		matchObj.setRequestPath(urlPath);

		List<CachedResponse> items = getMatching(matchObj);
		CachedResponse response = null;
		if (items != null && !items.isEmpty()) {
			response = items.get(0);
		}
		return response;
	}

}
