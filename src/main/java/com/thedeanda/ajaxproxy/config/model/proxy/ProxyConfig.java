package com.thedeanda.ajaxproxy.config.model.proxy;

import com.thedeanda.ajaxproxy.config.model.StringVariable;
import lombok.Getter;
import lombok.Setter;

public abstract class ProxyConfig {

	@Getter
	@Setter
	private Integer id;


	abstract public StringVariable getPath();

	abstract public boolean isEnableCache();

	abstract public int getCacheDuration();
}
