package com.thedeanda.ajaxproxy.model.config;

import com.thedeanda.ajaxproxy.config.ConfigLoaderV1;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.javajson.JsonObject;

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

	public ProxyConfig readProxyConfig(JsonObject json) {
		return v1Loader.readProxyConfig(json);
	}

	public JsonObject toJson(ProxyConfigRequest config) {
		JsonObject json = new JsonObject();

		json.put(PROXY_PROTOCOL, config.getProtocol());
		json.put(PROXY_HOST, config.getHost().getOriginalValue());
		json.put(PROXY_PORT, config.getPort());
		json.put(PROXY_PATH, config.getPath().getOriginalValue());
		json.put(PROXY_CACHE, config.isEnableCache());
		json.put(PROXY_CACHE_DUR, config.getCacheDuration());
		json.put(PROXY_HOST_HEADER, config.getHostHeader());

		return json;
	}

	public JsonObject toJson(ProxyConfigFile config) {
		JsonObject json = new JsonObject();

		json.put(PROXY_BASE_PATH, config.getBasePath().getOriginalValue());
		json.put(PROXY_FILTER_PATH, config.getFilterPath());
		json.put(PROXY_PATH, config.getPath().getOriginalValue());

		return json;
	}
}
