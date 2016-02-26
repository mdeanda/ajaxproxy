package com.thedeanda.ajaxproxy.service;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "resource")
public class StoredResource {
	@DatabaseField(id = true, width = 64)
	private String id;
	@DatabaseField(width = 1024 * 4)
	private String url;
	@DatabaseField(width = 32)
	private String method;

	@DatabaseField(width = 1024 * 4)
	private String headers;
	@DatabaseField(dataType = DataType.SERIALIZABLE, width = 1024 * 1024)
	// 1MB max
	private byte[] input;

	@DatabaseField()
	private int status;
	@DatabaseField(width = 1024 * 2)
	private String reason;

	@DatabaseField()
	private long startTime;
	@DatabaseField()
	private long duration;

	@DatabaseField(width = 1024 * 4)
	private String responseHeaders;
	@DatabaseField(dataType = DataType.SERIALIZABLE, width = 1024 * 1024 * 4)
	// 4MB max
	private byte[] output;

	@DatabaseField(width = 1024 * 2)
	private String errorMessage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(String responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public byte[] getOutput() {
		return output;
	}

	public void setOutput(byte[] output) {
		this.output = output;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
