package com.thedeanda.ajaxproxy.config.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StringVariable {
	private String originalValue;
	private String value;
}
