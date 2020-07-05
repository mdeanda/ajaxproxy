package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.*;
import com.thedeanda.ajaxproxy.config.model.proxy.HttpHeader;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ConfigLoaderV1 implements Loader {
	private static final int VERSION = 1;
	private static final String VAR_KEY = "variables";
	private static final String MODE = "mode";

	private static final String PROXY_PROTOCOL = "protocol";
	private static final String PROXY_HOST = "host";
	private static final String PROXY_PORT = "port";
	private static final String PROXY_PATH = "path";
	private static final String PROXY_CACHE = "cache";
	private static final String PROXY_CACHE_DUR = "cacheDuration";
	private static final String PROXY_HOST_HEADER = "hostHeader";
	private static final String PROXY_HEADERS = "headers";
	private static final String PROXY_HEADERS_NAME = "name";
	private static final String PROXY_HEADERS_VALUE = "value";

	private static final String PROXY_BASE_PATH = "basePath";
	private static final String PROXY_FILTER_PATH = "filterPath";

	@Override
	public Config loadConfig(JsonObject config, File workingDir) {
		if (!compatibleVersion(config)) {
			log.debug("incompatible version");
			return null;
		}

		List<Variable> variables;
		variables = loadVars(config);

		// variables _can_ be empty
		/*
		 * if (CollectionUtils.isEmpty(variables)) {
		 * log.debug("nothing recognized in config, return null config"); return null; }
		 * //
		 */

		ServerConfig server = loadServer(variables, config);

		return Config.builder()
				.variables(variables)
				.workingDir(workingDir.getAbsolutePath())
				.servers(Arrays.asList(server))
				.version(VERSION)
				.build();
	}

	private ServerConfig loadServer(List<Variable> variables, JsonObject config) {
		VariableHandler handler = new VariableHandler(variables);

		String sPort = config.getString("port");
		IntVariable portVar = handler.varForInt(sPort, 0);
		String sResourceBase = config.getString("resourceBase");
		StringVariable resourceBase = handler.varForString(sResourceBase);
		boolean showIndex = config.getBoolean("showIndex");
		int forcedLatencyMs = getLatency(config.getJsonObject("options"));
		int cacheTimeSec = getCacheTime(config.getJsonObject("options"));

		List<MergeConfig> mergeConfig = loadMergeConfig(handler, config);
		List<ProxyConfig> proxyConfig = loadProxyConfig(handler, config);

		ServerConfig server = ServerConfig.builder().port(portVar).resourceBase(resourceBase).showIndex(showIndex)
				.mergeConfig(mergeConfig).forcedLatencyMs(forcedLatencyMs).cacheTimeSec(cacheTimeSec)
				.proxyConfig(proxyConfig).build();

		return server;
	}

	private List<MergeConfig> loadMergeConfig(VariableHandler handler, JsonObject config) {
		List<MergeConfig> merges = new ArrayList<>();
		if (config.isJsonArray("merge")) {
			for (JsonValue v : config.getJsonArray("merge")) {
				JsonObject json = v.getJsonObject();
				StringVariable filePath = handler.varForString(json.getString("filePath"));
				StringVariable path = handler.varForString(json.getString("path"));
				boolean minify = json.getBoolean("minify");
				// TODO: mode
				MergeMode mode = json.hasKey(MODE) ? MergeMode.valueOf(json.getString(MODE)) : MergeMode.PLAIN;

				merges.add(MergeConfig.builder().filePath(filePath).path(path).minify(minify).mode(mode).build());
			}
		}

		return merges;
	}

	private int getLatency(JsonObject json) {
		if (json == null)
			return 0;
		int val = json.getInt("forcedLatency");
		switch (val) {
		case 1:
			return 100;
		case 2:
			return 250;
		case 3:
			return 500;
		case 4:
			return 1000;
		case 5:
			return 2000;
		case 6:
			return 5000;
		case 7:
			return 10000;
		case 8:
			return 30000;
		default:
			return 0;
		}
	}

	private int getCacheTime(JsonObject json) {
		if (json == null)
			return 0;
		int val = json.getInt("cacheTime");
		switch (val) {
		case 1:
			return 10;
		case 2:
			return 30;
		case 3:
			return 60;
		case 4:
			return 300;
		case 5:
			return 600;
		case 6:
			return 3600;
		default:
			return 0;
		}
	}

	private boolean compatibleVersion(JsonObject config) {
		if (config == null)
			return false;

		if (config.hasKey("version") && config.getInt("version") != 1) {
			return false;
		}
		// TODO: add check for a min set of fields
		if (config.hasKey("port"))
			return true;

		return false;
	}

	private List<Variable> loadVars(JsonObject config) {
		JsonObject vars = config.getJsonObject(VAR_KEY);
		List<Variable> ret = new ArrayList<>();

		if (vars != null && vars.size() > 0) {
			for (String key : vars) {
				Variable var = Variable.builder().key(key).value(vars.getString(key)).build();
				ret.add(var);
			}
		}

		return ret;
	}

	private List<ProxyConfig> loadProxyConfig(VariableHandler handler, JsonObject config) {
		List<ProxyConfig> proxyConfig = new ArrayList<>();
		JsonArray configs = config.getJsonArray("proxy");
		for (JsonValue v : configs) {
			ProxyConfig cfg = readProxyConfig(handler, v.getJsonObject());
			if (cfg != null) {
				proxyConfig.add(cfg);
			}
		}

		return proxyConfig;
	}

	private ProxyConfig readProxyConfig(VariableHandler handler, JsonObject json) {
		if (json.hasKey(PROXY_BASE_PATH)) {
			ProxyConfigFile config = new ProxyConfigFile();
			StringVariable pathVar = handler.varForString(json.getString(PROXY_PATH));
			StringVariable basePathVar = handler.varForString(json.getString(PROXY_BASE_PATH));
			config.setPath(pathVar);
			config.setBasePath(basePathVar);
			config.setFilterPath(json.getString(PROXY_FILTER_PATH));
			return config;
		} else {
			ProxyConfigRequest config = new ProxyConfigRequest();

			if (json.hasKey(PROXY_PROTOCOL)) {
				config.setProtocol(json.getString(PROXY_PROTOCOL));
			}

			if (json.hasKey(PROXY_HOST)) {
				StringVariable hostVar = handler.varForString(json.getString(PROXY_HOST));
				config.setHost(hostVar);
			}

			if (json.isInt(PROXY_PORT))
				config.setPort(json.getInt(PROXY_PORT));
			else {
				try {
					String value = json.getString(PROXY_PORT);
					config.setPort(Integer.parseInt(value));
				} catch (NumberFormatException nfe) {
					config.setPort(0);
				}
			}
			StringVariable pathVar = handler.varForString(json.getString(PROXY_PATH));
			config.setPath(pathVar);
			config.setEnableCache(json.getBoolean(PROXY_CACHE));
			config.setCacheDuration(json.getInt(PROXY_CACHE_DUR));
			config.setHostHeader(json.getString(PROXY_HOST_HEADER));

			if (json.isJsonArray(PROXY_HEADERS)) {
				JsonValue headersValue = json.get(PROXY_HEADERS);
				JsonArray headers = headersValue.getJsonArray();
				for (JsonValue v : headers) {
					JsonObject headerObj = v.getJsonObject();
					String name = headerObj.getString(PROXY_HEADERS_NAME);
					String value = headerObj.getString(PROXY_HEADERS_VALUE);
					HttpHeader hdr = HttpHeader.builder().name(name).value(value).build();
					config.getHeaders().add(hdr);
				}
			}
			return config;
		}
	}

	/**
	 * TODO: remove this when the ui uses the new data structure
	 * 
	 */
	public ProxyConfig readProxyConfig(JsonObject json) {
		if (json.hasKey(PROXY_BASE_PATH)) {
			ProxyConfigFile config = new ProxyConfigFile();
			config.setPath(StringVariable.builder().originalValue(json.getString(PROXY_PATH)).build());
			config.setBasePath(StringVariable.builder().originalValue(json.getString(PROXY_BASE_PATH)).build());
			config.setFilterPath(json.getString(PROXY_FILTER_PATH));
			return config;
		} else {
			ProxyConfigRequest config = new ProxyConfigRequest();

			if (json.hasKey(PROXY_PROTOCOL))
				config.setProtocol(json.getString(PROXY_PROTOCOL));

			if (json.hasKey(PROXY_HOST))
				config.setHost(StringVariable.builder().originalValue(json.getString(PROXY_HOST)).build());

			if (json.isInt(PROXY_PORT))
				config.setPort(json.getInt(PROXY_PORT));
			else {
				try {
					String value = json.getString(PROXY_PORT);
					config.setPort(Integer.parseInt(value));
				} catch (NumberFormatException nfe) {
					config.setPort(0);
				}
			}
			config.setPath(StringVariable.builder().originalValue(json.getString(PROXY_PATH)).build());
			config.setEnableCache(json.getBoolean(PROXY_CACHE));
			config.setCacheDuration(json.getInt(PROXY_CACHE_DUR));
			config.setHostHeader(json.getString(PROXY_HOST_HEADER));

			if (json.isJsonArray(PROXY_HEADERS)) {
				JsonValue headersValue = json.get(PROXY_HEADERS);
				JsonArray headers = headersValue.getJsonArray();
				for (JsonValue v : headers) {
					JsonObject headerObj = v.getJsonObject();
					String name = headerObj.getString(PROXY_HEADERS_NAME);
					String value = headerObj.getString(PROXY_HEADERS_VALUE);
					HttpHeader hdr = HttpHeader.builder().name(name).value(value).build();
					config.getHeaders().add(hdr);
				}
			}
			return config;
		}
	}

}
