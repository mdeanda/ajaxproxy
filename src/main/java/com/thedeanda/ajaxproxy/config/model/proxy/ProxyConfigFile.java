package com.thedeanda.ajaxproxy.config.model.proxy;

import com.thedeanda.ajaxproxy.config.model.StringVariable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProxyConfigFile implements ProxyConfig {
	@Builder.Default
	private StringVariable path = new StringVariable();
	@Builder.Default
	private StringVariable basePath = new StringVariable(); // for files
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
