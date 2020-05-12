package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProxyConfigFileDto extends ProxyConfigDto {

	private StringVariableDto path;
	private StringVariableDto basePath;
	private String filterPath;
	private boolean enableCache;
	private int cacheDuration;

}
