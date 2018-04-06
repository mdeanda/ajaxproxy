package com.thedeanda.ajaxproxy.config;

import java.io.InputStream;

import com.thedeanda.javajson.JsonObject;

public class JsonLoader {
	public static JsonObject load(String classPath) throws Exception {
		try (InputStream is = JsonLoader.class.getResourceAsStream(classPath)) {
			return JsonObject.parse(is);
		}
	}
}
