package com.thedeanda.ajaxproxy.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.javajson.JsonObject;

public class ConfigLoader {
	private List<Loader> loaders;

	public ConfigLoader() {
		loaders = new ArrayList<>();
		loaders.add(new ConfigLoaderV1());
	}

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
