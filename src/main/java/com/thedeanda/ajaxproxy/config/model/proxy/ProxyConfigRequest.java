package com.thedeanda.ajaxproxy.config.model.proxy;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ProxyConfigRequest implements ProxyConfig {
	private String protocol = "http";
	private String host;
	private int port = 80;
	private String path;
	private boolean enableCache;
	/** cache duration in seconds */
	private int cacheDuration = 500;
	private String hostHeader;
	private List<HttpHeader> headers = new ArrayList<>();

}
