package com.thedeanda.ajaxproxy.model.config;

import lombok.Data;

//NOTE: only used as ui dto, move to ui.variable.model package
@Data
public class Variable {
	private String key;
	private String value;
}
