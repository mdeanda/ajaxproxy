package com.thedeanda.ajaxproxy.config.model.proxy;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyConfigRequest implements ProxyConfig {
	@Builder.Default
	private String protocol = "http";
	private String host;
	@Builder.Default
	private int port = 80;
	private String path;
	private boolean enableCache;
	/** cache duration in seconds */
	@Builder.Default
	private int cacheDuration = 500;
	private String hostHeader;
	@Builder.Default
	private List<HttpHeader> headers = new ArrayList<>();

}
