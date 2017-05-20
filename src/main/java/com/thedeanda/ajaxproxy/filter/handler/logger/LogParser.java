package com.thedeanda.ajaxproxy.filter.handler.logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonValue;

public class LogParser implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(LogParser.class);
	private byte[] data;

	public LogParser(byte[] data) {
		this.data = data;
	}

	@Override
	public void run() {
		InputStream is = new ByteArrayInputStream(data);
		JsonArray array;
		try {
			array = JsonArray.parse(is);
			handleLogMessages(array);
		} catch (JsonException e) {
			log.warn(e.getMessage(), e);
		}

	}

	private void handleLogMessages(JsonArray array) {
		log.warn("array data: {}", array);
		for (JsonValue v : array) {
			
		}
	}
}
