package com.thedeanda.ajaxproxy.ui.model;

import com.thedeanda.ajaxproxy.LoadedResource;

public class Resource {
	/**
	 * old stuff, should go away if new proxy works
	 */
	private LoadedResource loadedResource;

	public Resource(LoadedResource lr) {
		loadedResource = lr;
	}

	public LoadedResource getLoadedResource() {
		return loadedResource;
	}

	public void setLoadedResource(LoadedResource loadedResource) {
		this.loadedResource = loadedResource;
	}

}
