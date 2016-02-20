package com.thedeanda.ajaxproxy.cache;

import static org.junit.Assert.*;

import org.junit.Test;

public class LruCacheTest {

	@Test
	public void test() {
		LruCache<String, String> cache = new LruCache<>(2);
		String key1 = "key1";
		String key2 = "key2";
		String key3 = "key3";

		String tmpValue = cache.get(key1);
		assertNull(tmpValue);

		cache.put(key1, key1);
		tmpValue = cache.get(key1);
		assertEquals(key1, tmpValue);

		cache.put(key2, key2);
		tmpValue = cache.get(key2);
		assertEquals(key2, tmpValue);

		cache.put(key3, key3);
		tmpValue = cache.get(key3);
		assertEquals(key3, tmpValue);

		tmpValue = cache.get(key2);
		assertEquals(key2, tmpValue);

		tmpValue = cache.get(key1);
		assertNull(tmpValue);
	}
}
