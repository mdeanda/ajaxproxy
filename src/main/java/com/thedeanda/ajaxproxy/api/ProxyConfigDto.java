package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyConfigDto {

	private int id;
	private StringVariableDto path;
	private boolean enableCache;
	private int cacheDuration;

}
