package com.thedeanda.ajaxproxy.model;

public class ProxyPath {
	private String domain;
	private String path;
	private int port;
	private boolean newProxy;

	public ProxyPath() {

	}

	public ProxyPath(String domain, int port, String path, boolean newProxy) {
		this.domain = domain;
		this.port = port;
		this.path = path;
		this.newProxy = newProxy;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isNewProxy() {
		return newProxy;
	}

	public void setNewProxy(boolean newProxy) {
		this.newProxy = newProxy;
	}
}