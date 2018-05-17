package com.thedeanda.ajaxproxy.config.model.proxy;

public interface ProxyConfig {
	public String getPath();

	public boolean isEnableCache();

	public int getCacheDuration();
}
