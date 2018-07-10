package com.thedeanda.ajaxproxy.config.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Variable {
	private String key;
	private String value;
}
