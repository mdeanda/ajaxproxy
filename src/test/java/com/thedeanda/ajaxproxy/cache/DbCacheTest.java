package com.thedeanda.ajaxproxy.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thedeanda.ajaxproxy.cache.model.CachedResponse;

public class DbCacheTest {
	private static final String CACHE_PATH = "./src/test/resources/DB_CACHE";
	private static final String PATH = "/foo/bar";
	private static final String URL = "http://example.com/foo/bar";
	private static final String reason = "reason";
	private static final String REASON = "reason";
	private static final int STATUS = 200;
	private static final long TIMESTAMP = 3209l;

	private final File cacheDir = new File(CACHE_PATH);
	private DbCache cache;

	@Before
	public void init() throws IOException {
		done();

		cache = new DbCache(new File(cacheDir, "test"));
	}

	@After
	public void done() throws IOException {
		//FileUtils.deleteDirectory(cacheDir);
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
}
