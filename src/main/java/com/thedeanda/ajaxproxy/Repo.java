package com.thedeanda.ajaxproxy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Repo<T> {
	private static final Logger log = LoggerFactory.getLogger(Repo.class);

	protected File dbFile;
	protected JdbcConnectionSource connectionSource;
	protected Dao<T, Integer> dao;
	protected Class<T> typeParameterClass;

	private boolean server;

	public Repo(File dbFile, Class<T> typeParameterClass) {
		this(dbFile, typeParameterClass, true);
	}

	public Repo(File dbFile, Class<T> typeParameterClass, boolean server) {
		this.dbFile = dbFile;
		this.typeParameterClass = typeParameterClass;
		this.server = server;
		try {
			initConnection();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			dao = null;
		}
	}

	protected void initConnection() throws SQLException {
		// TODO: thread safety
		if (connectionSource != null) {
			return;
		}

		try {
			String databaseUrl = "jdbc:h2:file:" + dbFile.getAbsolutePath();
			if (server) {
				databaseUrl += ";AUTO_SERVER=TRUE";
			}
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
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	public void deleteAll() throws SQLException {
		initConnection();
		TableUtils.clearTable(connectionSource, typeParameterClass);
	}

	public int save(T item) {
		int id = 0;
		try {
			initConnection();
			id = dao.create(item);
			log.warn("saved with id: {}", id);
		} catch (SQLException e) {
			// TODO: throw exception to caller...
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

	public List<T> getMatching(Map<String, Object> fieldValues) {
		List<T> response = null;
		try {
			initConnection();
			response = dao.queryForFieldValuesArgs(fieldValues);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return response;
	}
}
