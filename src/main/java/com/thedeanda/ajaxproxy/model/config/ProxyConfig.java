package com.thedeanda.ajaxproxy.model.config;

public class ProxyConfig {
	private String host;
	private int port;
	private String path;
	private boolean newProxy;
	private boolean enableCache;

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

	public boolean isNewProxy() {
		return newProxy;
	}

	public void setNewProxy(boolean newProxy) {
		this.newProxy = newProxy;
	}

	public boolean isEnableCache() {
		return enableCache;
	}

	public void setEnableCache(boolean enableCache) {
		this.enableCache = enableCache;
	}
}
