package com.thedeanda.ajaxproxy.config.model.proxy;

import com.thedeanda.ajaxproxy.config.model.StringVariable;

public interface ProxyConfig {
	public int getIndex(); //used only to show which proxy got used
	public void setIndex(int index);

	public StringVariable getPath();

	public boolean isEnableCache();

	public int getCacheDuration();
}
