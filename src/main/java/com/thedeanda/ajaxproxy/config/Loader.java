package com.thedeanda.ajaxproxy.config;

import java.io.File;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.javajson.JsonObject;

public interface Loader {
	Config loadConfig(JsonObject config, File workingDir);
}
