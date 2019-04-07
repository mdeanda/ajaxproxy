package com.thedeanda.ajaxproxy.config.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Variable implements Comparable<Variable> {
	private String key;
	private String value;

	@Override
	public int compareTo(Variable o) {
		return key.compareTo(o.key);
	}
}
