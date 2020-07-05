package com.thedeanda.ajaxproxy.config.model.proxy;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class HttpHeader {
	private String name;
	private String value;

	public static List<HttpHeader> fromString(String input) {
		List<HttpHeader> ret = new ArrayList<>();
		if (!StringUtils.isBlank(input)) {
			String[] lines = StringUtils.split(input, "\n");
			for (String line : lines) {
				String[] parts = StringUtils.split(line, ":", 2);
				ret.add(HttpHeader.builder().name(parts[0]).value(parts[1]).build());
			}
		}
		return ret;
	}

}
