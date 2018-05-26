package com.thedeanda.ajaxproxy.config.model.proxy;

import com.thedeanda.ajaxproxy.config.model.StringVariable;

import lombok.Data;

@Data
public class ProxyConfigFile implements ProxyConfig {
	private StringVariable path;
	private StringVariable basePath; // for files
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
