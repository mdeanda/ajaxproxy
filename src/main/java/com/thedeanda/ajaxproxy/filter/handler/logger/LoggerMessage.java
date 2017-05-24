package com.thedeanda.ajaxproxy.filter.handler.logger;

import com.thedeanda.javajson.JsonArray;

public class LoggerMessage {
	private String tag;
	private long ts;
	private long time;
	private String uid;
	private JsonArray message;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public JsonArray getMessage() {
		return message;
	}

	public void setMessage(JsonArray message) {
		this.message = message;
	}
}
