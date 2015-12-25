package com.thedeanda.ajaxproxy.ui.proxy;

public interface EditorListener {
	public void commitChanges(String host, int port, String path,
			boolean newProxy);
}
