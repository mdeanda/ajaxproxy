package com.thedeanda.ajaxproxy.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProxyPath {
	private String domain;
	private String path;
	private int port;

}