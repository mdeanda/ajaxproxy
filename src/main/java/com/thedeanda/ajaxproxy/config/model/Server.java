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

	@Builder.Default
	private List<MergeConfig> mergeConfig = new ArrayList<>();
}
