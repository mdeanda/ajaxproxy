package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyConfigRequestDto extends ProxyConfigDto {

	private String protocol;
	private StringVariableDto host;
	private int port;
	private StringVariableDto path;
	private boolean enableCache;
	private int cacheDuration;
	private String hostHeader;

}
