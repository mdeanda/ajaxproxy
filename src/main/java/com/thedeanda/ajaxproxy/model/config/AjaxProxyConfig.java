package com.thedeanda.ajaxproxy.model.config;

import java.util.ArrayList;
import java.util.List;

public class AjaxProxyConfig {
	private int port;
	private String resourceBase;
	private boolean showIndex;
	private List<ProxyConfig> proxyConfig = new ArrayList<>();

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
}
