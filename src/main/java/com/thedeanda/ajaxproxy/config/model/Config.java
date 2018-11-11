package com.thedeanda.ajaxproxy.config.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Config {
	private int version;
	private String workingDir;
	private List<ServerConfig> servers;
	private List<Variable> variables;
}
