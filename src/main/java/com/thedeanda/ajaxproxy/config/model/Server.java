package com.thedeanda.ajaxproxy.config.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Server {
	private IntVariable port;
	private StringVariable resourceBase;
	private boolean showIndex;
	
	//TODO: allow cache time, force latency per proxy
	private int cacheTimeSec;
	private int forcedLatencyMs;

	@Builder.Default
	private List<MergeConfig> mergeConfig = new ArrayList<>();
}
