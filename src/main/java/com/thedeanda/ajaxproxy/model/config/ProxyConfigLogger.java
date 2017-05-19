package com.thedeanda.ajaxproxy.model.config;

public class ProxyConfigLogger implements ProxyConfig {
	private String path;
	
	public ProxyConfigLogger(String path) {
		this.setPath(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	final public boolean isEnableCache() {
		return false;
	}

	final public int getCacheDuration() {
		return 0;
	}
}
