package com.thedeanda.ajaxproxy.config.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MergeConfig {
	StringVariable filePath;
	StringVariable path;
	boolean minify;
	// "mode":"CSS",
}
