package com.thedeanda.ajaxproxy.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> {
	private Cache<K, V> cache;

	public LruCache(int capacity) {
		cache = new Cache<>(capacity);
	}

	public V get(K key) {
		return cache.get(key);
	}

	public void put(K key, V value) {
		cache.put(key, value);
	}

	public void remove(K key) {
		cache.remove(key);
	}
}

class Cache<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 5921483156905505836L;
	private int capacity;

	public Cache(int capacity) {
		super(capacity, 1.5f, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > capacity;
	}
}
