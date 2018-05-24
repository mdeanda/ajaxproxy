package com.thedeanda.ajaxproxy.config.model.proxy;

import lombok.Builder;
import lombok.Data;

@Data
public class ProxyConfigLogger implements ProxyConfig {
	@Builder.Default
	private String path = "/logger";

	@Override
	final public boolean isEnableCache() {
		return false;
	}

	@Override
	final public int getCacheDuration() {
		return 0;
	}
}
