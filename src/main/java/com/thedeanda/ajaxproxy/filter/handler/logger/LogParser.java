package com.thedeanda.ajaxproxy.filter.handler.logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class LogParser implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(LogParser.class);
	private byte[] data;
	private LoggerMessageListener listener;

	public LogParser(byte[] data, LoggerMessageListener listener) {
		this.data = data;
		this.listener = listener;
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
		log.debug("array data: {}", array);
		for (JsonValue v : array) {
			if (v.isJsonObject()) {
				LoggerMessage msg = parse(v.getJsonObject());
				notifyListener(msg);
			} else {
				log.warn("Couldn't parse: {}", v);
			}
		}
	}

	private LoggerMessage parse(JsonObject json) {
		LoggerMessage ret = new LoggerMessage();
		ret.setTag(json.getString("tag"));
		ret.setTime(json.getLong("time"));
		ret.setTs(json.getLong("ts"));
		ret.setUid(json.getString("uid"));
		ret.setIndex(json.getInt("index"));
		ret.setMessage(json.getJsonArray("message"));

		log.debug("parsed object: {}", ret);
		return ret;
	}

	private void notifyListener(LoggerMessage message) {
		try {
			listener.messageReceived(message);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}
}
