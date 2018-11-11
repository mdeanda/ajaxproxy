package com.thedeanda.ajaxproxy.config;

import com.thedeanda.javajson.JsonObject;

import java.io.InputStream;

public class JsonLoader {
	public static JsonObject load(String classPath) throws Exception {
		try (InputStream is = JsonLoader.class.getResourceAsStream(classPath)) {
			return JsonObject.parse(is);
		}
	}
}
