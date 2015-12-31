package com.thedeanda.ajaxproxy.model.config;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class Convertor {
	private static Convertor instance;

	public static final String AP_RESOURCE_BASE = "resourceBase";
	public static final String AP_PORT = "port";
	public static final String AP_SHOW_INDEX = "showIndex";
	public static final String AP_PROXY = "proxy";
	public static final String AP_VARIABLES = "variables";

	@Deprecated
	public static final String PROXY_HOST_LEGACY = "domain"; // going away soon
	public static final String PROXY_HOST = "host";
	public static final String PROXY_PORT = "port";
	public static final String PROXY_PATH = "path";
	public static final String PROXY_NEWPROXY = "newProxy";
	public static final String PROXY_CACHE = "cache";

	private Convertor() {

	}

	public static Convertor get() {
		if (instance == null) {
			instance = new Convertor();
		}
		return instance;
	}

	public void processVariables(AjaxProxyConfig config) {
		Map<String, String> vars = config.getVariables();
		List<ProxyConfig> configs = config.getProxyConfig();
		for (ProxyConfig proxyConfig : configs) {
			processVariables(proxyConfig, vars);
		}
	}

	public void processVariables(ProxyConfig config, Map<String, String> vars) {
		for (String key : vars.keySet()) {
			String var = "${" + key + "}";
			String val = vars.get(key);

			String configVal;
			configVal = config.getHost();
			configVal = configVal.replaceAll(Pattern.quote(var), val);
			config.setHost(configVal);

			configVal = config.getPath();
			configVal = configVal.replaceAll(Pattern.quote(var), val);
			config.setPath(configVal);
		}

	}

	public AjaxProxyConfig readAjaxProxyConfig(JsonObject json) {
		AjaxProxyConfig config = new AjaxProxyConfig();

		config.setResourceBase(json.getString(AP_RESOURCE_BASE));
		config.setPort(json.getInt(AP_PORT));
		config.setShowIndex(json.getBoolean(AP_SHOW_INDEX));

		List<ProxyConfig> proxies = config.getProxyConfig();
		for (JsonValue val : json.getJsonArray(AP_PROXY)) {
			ProxyConfig pc = readProxyConfig(val.getJsonObject());
			proxies.add(pc);
		}

		if (json.isJsonObject(AP_VARIABLES)) {
			JsonObject vars = json.getJsonObject(AP_VARIABLES);
			for (String key : vars) {
				config.getVariables().put(key, vars.getString(key));
			}
		}
		return config;
	}

	public ProxyConfig readProxyConfig(JsonObject json) {
		ProxyConfig config = new ProxyConfig();

		config.setHost(json.getString(PROXY_HOST_LEGACY));
		if (json.hasKey(PROXY_HOST))
			config.setHost(json.getString(PROXY_HOST));

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
		config.setPath(json.getString(PROXY_PATH));
		config.setNewProxy(json.getBoolean(PROXY_NEWPROXY));
		config.setEnableCache(json.getBoolean(PROXY_CACHE));

		return config;
	}

	public JsonObject toJson(ProxyConfig config) {
		JsonObject json = new JsonObject();

		json.put(PROXY_HOST_LEGACY, config.getHost());
		json.put(PROXY_HOST, config.getHost());
		json.put(PROXY_PORT, config.getPort());
		json.put(PROXY_PATH, config.getPath());
		json.put(PROXY_NEWPROXY, config.isNewProxy());
		json.put(PROXY_CACHE, config.isEnableCache());

		return json;
	}
}
