package com.thedeanda.ajaxproxy.config.model.proxy;

import com.thedeanda.ajaxproxy.config.model.StringVariable;

public interface ProxyConfig {
	public StringVariable getPath();

	public boolean isEnableCache();

	public int getCacheDuration();
}
