package com.thedeanda.ajaxproxy.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.Server;
import com.thedeanda.ajaxproxy.config.model.Variable;
import com.thedeanda.ajaxproxy.config.model.VariableValue;
import com.thedeanda.javajson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConfigLoaderV1 implements Loader {
	private static final String VAR_KEY = "variables";

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

		Server server = Server.builder()
				.port(VariableValue.builder().build())
				.build();

		return Config.builder().variables(variables).workingDir(workingDir.getAbsolutePath())
				.servers(Arrays.asList(server)).build();
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
