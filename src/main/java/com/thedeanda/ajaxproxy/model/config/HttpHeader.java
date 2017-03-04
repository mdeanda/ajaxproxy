package com.thedeanda.ajaxproxy.model.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class HttpHeader {
	private String name;
	private String value;

	public HttpHeader() {

	}

	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static List<HttpHeader> fromString(String input) {
		List<HttpHeader> ret = new ArrayList<>();
		if (!StringUtils.isBlank(input)) {
			String[] lines = StringUtils.split(input, "\n");
			for (String line : lines) {
				String[] parts = StringUtils.split(line, ":", 2);
				ret.add(new HttpHeader(parts[0], parts[1]));
			}
		}
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
