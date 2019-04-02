package com.thedeanda.ajaxproxy.ui.variable.model;

import lombok.*;

//NOTE: only used as ui dto, move to ui.variable.model package
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Variable {
	private String key;
	private String value;
}
