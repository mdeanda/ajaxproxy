package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.javajson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {
	private List<Loader> loaders;

	public ConfigLoader() {
		loaders = new ArrayList<>();
		loaders.add(new ConfigLoaderV1());
		loaders.add(new ConfigLoaderV2());
	}

	public Config loadConfig(JsonObject jsonConfig, File workingDir) {
		Config config = null;
		for (Loader loader : loaders) {
			config = loader.loadConfig(jsonConfig, workingDir);
			if (config != null) {
				break;
			}
		}

		config = migrate(config);

		return config;
	}

	public Config migrate(Config config) {
		if (config == null) return config;

		//migrate here
		int serverId = 0;
		int proxyId = 0;

		for (ServerConfig sc : config.getServers()) {
			sc.setId(serverId++);
			for (ProxyConfig pc : sc.getProxyConfig()) {
				pc.setId(proxyId++);
			}
		}



		return config;
	}

}
