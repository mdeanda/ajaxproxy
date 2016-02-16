package com.thedeanda.ajaxproxy.ui.rest;

import java.util.UUID;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rest_history")
public class HistoryItem {
	@DatabaseField(id = true, width = 64)
	private String id = UUID.randomUUID().toString();
	@DatabaseField(width = 1024 * 4)
	private String name;
	@DatabaseField(width = 1024)
	private String group;	
	@DatabaseField(width = 1024 * 4)
	private String url;
	@DatabaseField(width = 32)
	private String method;
	@DatabaseField(width = 1024 * 4)
	private String headers;
	@DatabaseField(dataType=DataType.SERIALIZABLE, width = 1024 * 1024)
	private byte[] input;

	@Override
	public String toString() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public byte[] getInput() {
		return input;
	}

	public void setInput(byte[] input) {
		this.input = input;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
