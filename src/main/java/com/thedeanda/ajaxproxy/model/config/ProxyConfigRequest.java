package com.thedeanda.ajaxproxy.model.config;

import java.util.ArrayList;
import java.util.List;

public class ProxyConfigRequest implements ProxyConfig {
	private String host;
	private int port;
	private String path;
	private boolean enableCache;
	/** cache duration in seconds */
	private int cacheDuration = 500;
	private List<CustomHeader> headers = new ArrayList<>();

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isEnableCache() {
		return enableCache;
	}

	public void setEnableCache(boolean enableCache) {
		this.enableCache = enableCache;
	}

	public int getCacheDuration() {
		return cacheDuration;
	}

	public void setCacheDuration(int cacheDuration) {
		this.cacheDuration = cacheDuration;
	}

	public List<CustomHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(List<CustomHeader> headers) {
		this.headers = headers;
	}
}
