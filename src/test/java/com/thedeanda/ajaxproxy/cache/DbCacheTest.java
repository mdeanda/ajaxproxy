package com.thedeanda.ajaxproxy.cache;

import com.thedeanda.ajaxproxy.cache.model.CachedResponse;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class DbCacheTest {
	private static final String CACHE_PATH = "./src/test/resources/DB_CACHE";
	private static final String PATH = "/foo/bar";
	private static final String URL = "http://example.com/foo/bar";
	private static final String REASON = "reason";
	private static final int STATUS = 200;
	private static final long TIMESTAMP = 3209l;

	private static final File cacheBaseDir = new File(CACHE_PATH);
	private File cacheDir;
	private DbCache cache;

	@Before
	public void init() throws Exception {
		cacheBaseDir.mkdirs();
		cacheDir = File.createTempFile("test", "", cacheBaseDir);
		cacheDir.delete();
		cache = new DbCache(cacheDir, false);
	}

	@AfterClass
	public static void classDone() {
		try {
			FileUtils.deleteDirectory(cacheBaseDir);
		} catch (IOException e) {

		}
	}

	@Test
	public void testInsert() {
		CachedResponse r = cache.get(PATH);
		assertNull(r);

		CachedResponse sample = new CachedResponse();
		sample.setRequestPath(PATH);
		sample.setUrl(URL);
		sample.setReason(REASON);
		sample.setStatus(STATUS);
		sample.setTimestamp(TIMESTAMP);
		cache.cache(sample);

		assertEquals(1, sample.getId());

		CachedResponse response = cache.get(1);
		assertNotNull(response);
		assertEquals(PATH, response.getRequestPath());
		assertEquals(URL, response.getUrl());
		assertEquals(STATUS, response.getStatus());
		assertEquals(REASON, response.getReason());
		assertEquals(TIMESTAMP, response.getTimestamp());
	}

	@Test
	public void testGetByUrl() {
		CachedResponse r = cache.get(PATH);
		assertNull(r);

		CachedResponse sample = new CachedResponse();
		sample.setRequestPath(PATH);
		sample.setUrl(URL);
		sample.setReason(REASON);
		sample.setStatus(STATUS);
		sample.setTimestamp(TIMESTAMP);
		cache.cache(sample);

		CachedResponse response = cache.get(URL);

		assertNotNull(response);
		assertEquals(PATH, response.getRequestPath());
		assertEquals(URL, response.getUrl());
		assertEquals(STATUS, response.getStatus());
		assertEquals(REASON, response.getReason());
		assertEquals(TIMESTAMP, response.getTimestamp());
	}
}
