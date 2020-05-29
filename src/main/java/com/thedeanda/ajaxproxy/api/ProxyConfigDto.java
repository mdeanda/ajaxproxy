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

	public enum ProxyType {
		Proxy, File, Logger
	}

	private int id;
	private StringVariableDto path;
	private boolean enableCache;
	private int cacheDuration;
	private ProxyType type;

}
