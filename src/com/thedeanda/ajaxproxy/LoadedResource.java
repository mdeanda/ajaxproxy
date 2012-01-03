package com.thedeanda.ajaxproxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;

import org.apache.commons.io.IOUtils;

public class LoadedResource {
	private String url;
	private long duration;
	private byte[] input;
	private byte[] output;
	private String method;
	private int statusCode;
	private List<Cookie> cookies;
	private Map<String, String> headers = new TreeMap<String, String>();

	@Override
	public String toString() {
		return url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public byte[] getInput() {
		return input;
	}

	public void setInput(byte[] input) {
		this.input = input;
	}

	public byte[] getOutput() {
		return output;
	}

	public void setOutput(byte[] output) {
		this.output = output;
	}

	public String getOutputAsText() {
		return convertToText(output);
	}

	public String getInputAsText() {
		return convertToText(input);
	}

	private String convertToText(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		if (bytes != null) {
			try {
				@SuppressWarnings("unchecked")
				List<String> lines = IOUtils
						.readLines(new ByteArrayInputStream(bytes));
				for (String line : lines) {
					sb.append(line);
					sb.append("\n");
				}
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void addHeader(String name, String header) {
		headers.put(name, header);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
