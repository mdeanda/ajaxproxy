package com.thedeanda.ajaxproxy.ui.serverconfig.proxy;

import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;

public interface EditorPanel <T extends ProxyConfig>{

	void setValue(T value);
	T getResult();

}
