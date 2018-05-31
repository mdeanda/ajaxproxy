package com.thedeanda.ajaxproxy.config.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Config {
	private int version;
	private String workingDir;
	private List<ServerConfig> servers;
	private List<Variable> variables;
}
