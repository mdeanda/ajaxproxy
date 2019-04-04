package com.thedeanda.ajaxproxy.ui.variable.model;

import lombok.*;

//NOTE: only used as ui dto, move to ui.variable.model package
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Variable implements Comparable<Variable>{
	private String key;
	private String value;

	@Override
	public int compareTo(Variable o) {
		return key.compareTo(o.key);
	}
}
