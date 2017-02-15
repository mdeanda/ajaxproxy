package com.thedeanda.ajaxproxy.model.config;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class Convertor {
	private static Convertor instance;

	public static final String AP_RESOURCE_BASE = "resourceBase";
	public static final String AP_PORT = "port";
	public static final String AP_SHOW_INDEX = "showIndex";
	public static final String AP_PROXY = "proxy";
	public static final String AP_VARIABLES = "variables";

	public static final String PROXY_HOST = "host";
	public static final String PROXY_PORT = "port";
	public static final String PROXY_PATH = "path";
	public static final String PROXY_CACHE = "cache";
	public static final String PROXY_CACHE_DUR = "cacheDuration";
	public static final String PROXY_HEADERS = "headers";
	public static final String PROXY_HEADERS_NAME = "name";
	public static final String PROXY_HEADERS_VALUE = "value";

	private static final String PROXY_BASE_PATH = "basePath";
	private static final String PROXY_FILTER_PATH = "filterPath";

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
			if (proxyConfig instanceof ProxyConfigRequest) {
				processVariables((ProxyConfigRequest) proxyConfig, vars);
			}
		}
	}

	public void processVariables(ProxyConfigRequest config, Map<String, String> vars) {
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
		if (json.hasKey(PROXY_BASE_PATH)) {
			ProxyConfigFile config = new ProxyConfigFile();
			config.setPath(json.getString(PROXY_PATH));
			config.setBasePath(json.getString(PROXY_BASE_PATH));
			config.setFilterPath(json.getString(PROXY_FILTER_PATH));
			return config;
		} else {
			ProxyConfigRequest config = new ProxyConfigRequest();

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
			config.setEnableCache(json.getBoolean(PROXY_CACHE));
			config.setCacheDuration(json.getInt(PROXY_CACHE_DUR));


			if (json.isJsonArray(PROXY_HEADERS)) {
				JsonValue headersValue = json.get(PROXY_HEADERS);
				JsonArray headers = headersValue.getJsonArray();
				for (JsonValue v : headers) {
					JsonObject headerObj = v.getJsonObject();
					String name = headerObj.getString(PROXY_HEADERS_NAME);
					String value = headerObj.getString(PROXY_HEADERS_VALUE);
					CustomHeader hdr = new CustomHeader(name, value);
					config.getHeaders().add(hdr);
				}
			}
			return config;
		}
	}

	public JsonObject toJson(ProxyConfigRequest config) {
		JsonObject json = new JsonObject();

		json.put(PROXY_HOST, config.getHost());
		json.put(PROXY_PORT, config.getPort());
		json.put(PROXY_PATH, config.getPath());
		json.put(PROXY_CACHE, config.isEnableCache());
		json.put(PROXY_CACHE_DUR, config.getCacheDuration());

		return json;
	}

	public JsonObject toJson(ProxyConfigFile config) {
		JsonObject json = new JsonObject();

		json.put(PROXY_BASE_PATH, config.getBasePath());
		json.put(PROXY_PATH, config.getPath());

		return json;
	}
}
