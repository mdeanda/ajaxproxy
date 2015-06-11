package com.thedeanda.ajaxproxy.ui.model;

import java.util.UUID;

import com.thedeanda.ajaxproxy.LoadedResource;

public class Resource {
	/**
	 * old stuff, should go away if new proxy works
	 */
	private LoadedResource loadedResource;
	private UUID id;
	private String url;
	private String method;

	public Resource(LoadedResource lr) {
		loadedResource = lr;
	}

	public Resource(UUID id, String url, String method) {
		this.id = id;
		this.url = url;
		this.method = method;
	}

	public LoadedResource getLoadedResource() {
		return loadedResource;
	}

	public void setLoadedResource(LoadedResource loadedResource) {
		this.loadedResource = loadedResource;
	}

	public UUID getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getMethod() {
		return method;
	}

}
