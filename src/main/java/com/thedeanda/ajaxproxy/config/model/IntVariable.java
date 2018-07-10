package com.thedeanda.ajaxproxy.config.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntVariable {
	private String originalValue;
	private int value;

	public IntVariable(int intValue) {
		this(String.valueOf(intValue), intValue);
	}
	
	public IntVariable(String originalValue, int intValue) {
		this.originalValue = originalValue;
		this.value = intValue;
	}
}
