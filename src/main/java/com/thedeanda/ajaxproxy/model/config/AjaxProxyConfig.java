package com.thedeanda.ajaxproxy.model.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getResourceBase() {
		return resourceBase;
	}

	public void setResourceBase(String resourceBase) {
		this.resourceBase = resourceBase;
	}

	public boolean isShowIndex() {
		return showIndex;
	}

	public void setShowIndex(boolean showIndex) {
		this.showIndex = showIndex;
	}

	public List<ProxyConfig> getProxyConfig() {
		return proxyConfig;
	}

	public void setProxyConfig(List<ProxyConfig> proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}
}
