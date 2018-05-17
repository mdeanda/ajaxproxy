package com.thedeanda.ajaxproxy.model.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;

import lombok.Data;

@Data
public class AjaxProxyConfig {
	private int port;
	private String resourceBase;
	private boolean showIndex;
	private List<ProxyConfig> proxyConfig = new ArrayList<>();
	private Map<String, String> variables = new HashMap<>();
	
	/**
	 * file points to this objects "saved" location to use as reference for
	 * relative paths
	 */
	private File configFile;

	// missing request delay, cache time, merge
	// add config version
}
