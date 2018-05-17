package com.thedeanda.ajaxproxy.model.config;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.thedeanda.ajaxproxy.config.ConfigLoaderV1;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class Convertor {
	private static Convertor instance;

	public static final String AP_RESOURCE_BASE = "resourceBase";
	public static final String AP_PORT = "port";
	public static final String AP_SHOW_INDEX = "showIndex";
	public static final String AP_PROXY = "proxy";
	public static final String AP_VARIABLES = "variables";

	public static final String PROXY_PROTOCOL = "protocol";
	public static final String PROXY_HOST = "host";
	public static final String PROXY_PORT = "port";
	public static final String PROXY_PATH = "path";
	public static final String PROXY_CACHE = "cache";
	public static final String PROXY_CACHE_DUR = "cacheDuration";
	public static final String PROXY_HOST_HEADER = "hostHeader";
	public static final String PROXY_HEADERS = "headers";
	public static final String PROXY_HEADERS_NAME = "name";
	public static final String PROXY_HEADERS_VALUE = "value";

	private static final String PROXY_BASE_PATH = "basePath";
	private static final String PROXY_FILTER_PATH = "filterPath";

	private ConfigLoaderV1 v1Loader = new ConfigLoaderV1();
	
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
		// logger gets added first so it has priority
		// TODO: externalize "/logger" path
		proxies.add(new ProxyConfigLogger("/logger"));
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
		return v1Loader.readProxyConfig(json);
	}

	public JsonObject toJson(ProxyConfigRequest config) {
		JsonObject json = new JsonObject();

		json.put(PROXY_PROTOCOL, config.getProtocol());
		json.put(PROXY_HOST, config.getHost());
		json.put(PROXY_PORT, config.getPort());
		json.put(PROXY_PATH, config.getPath());
		json.put(PROXY_CACHE, config.isEnableCache());
		json.put(PROXY_CACHE_DUR, config.getCacheDuration());
		json.put(PROXY_HOST_HEADER, config.getHostHeader());

		return json;
	}

	public JsonObject toJson(ProxyConfigFile config) {
		JsonObject json = new JsonObject();

		json.put(PROXY_BASE_PATH, config.getBasePath());
		json.put(PROXY_FILTER_PATH, config.getFilterPath());
		json.put(PROXY_PATH, config.getPath());

		return json;
	}
}
