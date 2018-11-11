package com.thedeanda.ajaxproxy;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class OrmLiteBinaryTest {

	@DatabaseTable(tableName = "accounts")
	public static class Account {
		@DatabaseField(id = true)
		private String name;

		@DatabaseField(canBeNull = false)
		private String password;

		@DatabaseField(dataType = DataType.BYTE_ARRAY, width = 1024)
		private byte[] bytes;

		Account() {
		}
	}

	@Test
	@Ignore
	public void testHsqldb() throws SQLException {
		String databaseUrl = "jdbc:hsqldb:mem:account";
		testBinary(databaseUrl);
	}

	@Test
	public void testH2() throws SQLException {
		String databaseUrl = "jdbc:h2:mem:account";
		testBinary(databaseUrl);
	}

	private void testBinary(String databaseUrl) throws SQLException {
		ConnectionSource connectionSource = new JdbcConnectionSource(
				databaseUrl);
		Dao<Account, String> accountDao = DaoManager.createDao(
				connectionSource, Account.class);
		TableUtils.createTable(connectionSource, Account.class);

		String name = "Jim Smith";
		Account account = new Account();
		account.name = name;
		account.password = "foo";
		account.bytes = new byte[] { 1, 2, 3 };

		accountDao.create(account);

		Account account2 = accountDao.queryForId(name);
		assertEquals("foo", account2.password);

		connectionSource.close();
	}
}
