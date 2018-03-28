package com.thedeanda.ajaxproxy.model.config;

import lombok.Data;

@Data
public class ProxyConfigFile implements ProxyConfig {
	private String path;
	private String basePath; // for files
	private String filterPath; // start of request path that gets removed

	@Override
	public boolean isEnableCache() {
		return false;
	}

	@Override
	public int getCacheDuration() {
		return 0;
	}

}
