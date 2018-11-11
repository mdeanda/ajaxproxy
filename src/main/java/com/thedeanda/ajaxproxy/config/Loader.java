package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.javajson.JsonObject;

import java.io.File;

public interface Loader {
	Config loadConfig(JsonObject config, File workingDir);
}
