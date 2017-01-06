package com.thedeanda.ajaxproxy.model.config;

public interface ProxyConfig {
	public String getPath();

	public boolean isEnableCache();

	public int getCacheDuration();
}
