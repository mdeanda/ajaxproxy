package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProxyConfigRequestDto extends ProxyConfigDto {

	private String protocol;
	private StringVariableDto host;
	private int port;
	private StringVariableDto path;
	private boolean enableCache;
	private int cacheDuration;
	private String hostHeader;

}
