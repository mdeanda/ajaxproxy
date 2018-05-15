package com.thedeanda.ajaxproxy.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.IntVariable;
import com.thedeanda.ajaxproxy.config.model.MergeConfig;
import com.thedeanda.ajaxproxy.config.model.MergeMode;
import com.thedeanda.ajaxproxy.config.model.Server;
import com.thedeanda.ajaxproxy.config.model.StringVariable;
import com.thedeanda.ajaxproxy.config.model.Variable;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConfigLoaderV1 implements Loader {
	private static final String VAR_KEY = "variables";
	private static final String MODE = "mode";

	@Override
	public Config loadConfig(JsonObject config, File workingDir) {
		if (!compatibleVersion(config)) {
			log.debug("incompatible version");
			return null;
		}

		List<Variable> variables;
		variables = loadVars(config);

		if (CollectionUtils.isEmpty(variables)) {
			log.debug("nothing recognized in config, return null config");
			return null;
		}

		Server server = loadServer(variables, config);

		return Config.builder().variables(variables).workingDir(workingDir.getAbsolutePath())
				.servers(Arrays.asList(server)).build();
	}

	private Server loadServer(List<Variable> variables, JsonObject config) {
		VariableHandler handler = new VariableHandler(variables);

		String sPort = config.getString("port");
		IntVariable portVar = handler.varForInt(sPort);
		String sResourceBase = config.getString("resourceBase");
		StringVariable resourceBase = handler.varForString(sResourceBase);
		boolean showIndex = config.getBoolean("showIndex");
		int forcedLatencyMs = getLatency(config.getJsonObject("options"));
		int cacheTimeSec = getCacheTime(config.getJsonObject("options"));

		List<MergeConfig> mergeConfig = loadMergeConfig(handler, config);

		Server server = Server.builder().port(portVar).resourceBase(resourceBase).showIndex(showIndex)
				.mergeConfig(mergeConfig).forcedLatencyMs(forcedLatencyMs).cacheTimeSec(cacheTimeSec).build();

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

		return true;
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

}
