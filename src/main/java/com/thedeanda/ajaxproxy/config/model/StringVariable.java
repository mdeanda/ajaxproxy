package com.thedeanda.ajaxproxy.config.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StringVariable {
	private String originalValue;
	private String value;

	public StringVariable(String originalValue, String value) {
		this.originalValue = originalValue;
		this.value = value;
	}
}
