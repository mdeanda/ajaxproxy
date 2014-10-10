package com.thedeanda.ajaxproxy.http;

import org.apache.http.Header;

public interface RequestListener {
	public void requestComplete(int status, Header[] responseHeaders,
			byte[] data);
}
