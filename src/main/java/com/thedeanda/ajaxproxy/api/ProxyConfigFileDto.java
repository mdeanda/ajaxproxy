package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyConfigFileDto extends ProxyConfigDto {

	private StringVariableDto path;
	private StringVariableDto basePath;
	private String filterPath;
	private boolean enableCache;
	private int cacheDuration;

}
