package com.thedeanda.ajaxproxy;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thedeanda.ajaxproxy.cache.model.CachedResponse;
import com.thedeanda.ajaxproxy.service.StoredResource;

public class Repo<T> {
	private static final Logger log = LoggerFactory.getLogger(Repo.class);

	protected File dbFile;
	protected JdbcConnectionSource connectionSource;
	protected Dao<T, Integer> dao;

	private Class<T> typeParameterClass;

	public Repo(File dbFile, Class<T> typeParameterClass) {
		this.dbFile = dbFile;
		this.typeParameterClass = typeParameterClass;
		try {
			initConnection();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			dao = null;
		}
	}

	protected void initConnection() throws SQLException {
		try {
			String databaseUrl = "jdbc:h2:file:" + dbFile.getAbsolutePath() + ";AUTO_SERVER=TRUE";
			connectionSource = new JdbcConnectionSource(databaseUrl);

			dao = DaoManager.createDao(connectionSource, typeParameterClass);

			if (!dao.isTableExists()) {
				TableUtils.createTableIfNotExists(connectionSource, typeParameterClass);
			}
		} catch (SQLException e) {
			log.warn(e.getMessage(), e);
		}
	}

	protected void closeConnection() {
		try {
			if (connectionSource != null)
				connectionSource.close();
		} catch (SQLException e) {
			log.warn(e.getMessage(), e);
		}
	}

	public int save(T item) {
		int id = 0;
		try {
			initConnection();
			id = dao.create(item);
			log.warn("saved with id: {}", id);
		} catch (SQLException e) {
			//TODO: throw exception to caller...
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return id;
	}

	public T get(Integer id) {
		T t = null;
		try {
			initConnection();
			t = dao.queryForId(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return t;
	}

	public List<T> getMatching(T matchObj) {
		List<T> response = null;
		try {
			initConnection();
			response = dao.queryForMatchingArgs(matchObj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return response;
	}
}
