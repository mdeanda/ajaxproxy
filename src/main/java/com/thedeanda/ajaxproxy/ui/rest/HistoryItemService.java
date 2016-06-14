package com.thedeanda.ajaxproxy.ui.rest;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thedeanda.ajaxproxy.ui.ConfigService;

public class HistoryItemService {
	private static final HistoryItemService instance = new HistoryItemService();

	private Dao<HistoryItem, String> dao;
	private ConnectionSource connectionSource;

	public static HistoryItemService get() {
		return instance;
	}

	private HistoryItemService() {
	}

	private void initConnection() throws SQLException {
		File dbFile = ConfigService.get().getRestHistoryDb();

		try {
			String databaseUrl = "jdbc:h2:file:" + dbFile.getAbsolutePath();
			connectionSource = new JdbcConnectionSource(databaseUrl);

			dao = DaoManager.createDao(connectionSource, HistoryItem.class);

			if (!dao.isTableExists()) {
				TableUtils.createTableIfNotExists(connectionSource,
						HistoryItem.class);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeConnection() {
		try {
			connectionSource.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(HistoryItem item) {
		try {
			initConnection();
			dao.create(item);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	public List<HistoryItem> list() {
		List<HistoryItem> items = new ArrayList<>();
		try {
			initConnection();
			items = dao.queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return items;
	}
}
