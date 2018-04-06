package com.thedeanda.ajaxproxy.config.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Server {
	private VariableValue port;
	private VariableValue resourceBase;
	private boolean showIndex;
}
