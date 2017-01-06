package com.thedeanda.ajaxproxy.ui.proxy;

import com.thedeanda.ajaxproxy.model.config.ProxyConfigRequest;

public interface EditorListener {
	public void commitChanges(ProxyConfigRequest config);
}
