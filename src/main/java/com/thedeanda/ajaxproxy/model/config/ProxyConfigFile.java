package com.thedeanda.ajaxproxy.model.config;

public class ProxyConfigFile implements ProxyConfig {
	private String path;
	private String basePath; // for files
	private String filterPath; // start of request path that gets removed

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	@Override
	public boolean isEnableCache() {
		return false;
	}

	@Override
	public int getCacheDuration() {
		return 0;
	}

	public String getFilterPath() {
		return filterPath;
	}

	public void setFilterPath(String filterPath) {
		this.filterPath = filterPath;
	}
}
