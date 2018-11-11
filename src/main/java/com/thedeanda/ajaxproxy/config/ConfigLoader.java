package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.javajson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {
	private List<Loader> loaders;

	public ConfigLoader() {
		loaders = new ArrayList<>();
		loaders.add(new ConfigLoaderV1());
	}

	/**
	 * TODO: change this so instead of returning the same object, it migrates from
	 * v1, to v2 so more version can be added later without having to modify old
	 * implementations.
	 */
	public Config loadConfig(JsonObject jsonConfig, File workingDir) {
		Config config = null;
		for (Loader loader : loaders) {
			config = loader.loadConfig(jsonConfig, workingDir);
			if (config != null) {
				return config;
			}
		}
		return null;
	}

}
