package com.thedeanda.ajaxproxy.config.model;

import java.util.ArrayList;
import java.util.List;

import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerConfig {
	private IntVariable port;
	private StringVariable resourceBase;
	private boolean showIndex;

	// TODO: allow cache time, force latency per proxy
	private int cacheTimeSec;
	private int forcedLatencyMs;

	@Builder.Default
	private List<MergeConfig> mergeConfig = new ArrayList<>();
	@Builder.Default
	private List<ProxyConfig> proxyConfig = new ArrayList<>();
}
