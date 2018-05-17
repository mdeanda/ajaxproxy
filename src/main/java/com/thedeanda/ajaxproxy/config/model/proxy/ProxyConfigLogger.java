package com.thedeanda.ajaxproxy.config.model.proxy;

import lombok.Data;

@Data
public class ProxyConfigLogger implements ProxyConfig {
	private String path;

	public ProxyConfigLogger(String path) {
		this.setPath(path);
	}

	@Override
	final public boolean isEnableCache() {
		return false;
	}

	@Override
	final public int getCacheDuration() {
		return 0;
	}
}
