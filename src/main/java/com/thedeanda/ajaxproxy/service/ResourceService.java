package com.thedeanda.ajaxproxy.service;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thedeanda.ajaxproxy.cache.LruCache;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.ConfigService;
import com.thedeanda.ajaxproxy.ui.model.Resource;
import com.thedeanda.ajaxproxy.ui.rest.HistoryItem;

public class ResourceService implements RequestListener {
	private static Logger log = LoggerFactory.getLogger(ResourceService.class);
	private final LruCache<UUID, Resource> cache;
	private JdbcConnectionSource connectionSource;
	private Dao<HistoryItem, Resource> dao;
	private File dbFile;

	public ResourceService(int cacheSize, File dbFile) {
		cache = new LruCache<UUID, Resource>(cacheSize);
		this.dbFile = dbFile;
	}

	private void initConnection() throws SQLException {

		try {
			// this uses h2 but you can change it to match your database
			String databaseUrl = "jdbc:h2:file:" + dbFile.getAbsolutePath();
			connectionSource = new JdbcConnectionSource(databaseUrl);

			dao = DaoManager.createDao(connectionSource, Resource.class);

			if (!dao.isTableExists()) {
				TableUtils.createTableIfNotExists(connectionSource,
						Resource.class);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(Resource resource) {
		log.debug("saving resource: {}", resource);
		// TODO: save to db
		cache.put(resource.getId(), resource);
	}

	public Resource get(UUID id) {
		Resource ret = cache.get(id);
		if (ret == null) {
			// TODO: get from db
		}
		return ret;
	}

	@Override
	public void newRequest(UUID id, String url, String method) {
		log.debug("new request: {} {} {}", id, url, method);
	}

	@Override
	public void startRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {
		log.debug("start request: {} {}", id, url);
	}

	@Override
	public void requestComplete(UUID id, int status, String reason,
			long duration, Header[] responseHeaders, byte[] data) {
		log.debug("request complete: {} {}", id, status);
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		log.debug("request error: {} {}", id, message);
	}

}
