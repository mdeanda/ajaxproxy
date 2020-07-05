package com.thedeanda.ajaxproxy.config.model.proxy;

import com.thedeanda.ajaxproxy.config.model.StringVariable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyConfigRequest extends ProxyConfig {
	@Builder.Default
	private String protocol = "http";
	@Builder.Default
	private StringVariable host = new StringVariable();
	@Builder.Default
	private int port = 80;
	@Builder.Default
	private StringVariable path = new StringVariable();
	private boolean enableCache;
	/** cache duration in seconds */
	@Builder.Default
	private int cacheDuration = 500;
	private String hostHeader;
	@Builder.Default
	private List<HttpHeader> headers = new ArrayList<>();

}
