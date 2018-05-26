package com.thedeanda.ajaxproxy.config.model.proxy;

import com.thedeanda.ajaxproxy.config.model.StringVariable;

import lombok.Builder;
import lombok.Data;

@Data
public class ProxyConfigLogger implements ProxyConfig {
	@Builder.Default
	private StringVariable path = new StringVariable("/logger", "/logger");

	@Override
	final public boolean isEnableCache() {
		return false;
	}

	@Override
	final public int getCacheDuration() {
		return 0;
	}
}
