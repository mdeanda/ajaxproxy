package com.thedeanda.ajaxproxy.http;

import java.net.URL;
import java.util.UUID;

import org.apache.http.Header;

public interface RequestListener {
	public void newRequest(UUID id, String url, String method);
	
	public void startRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data);

	public void requestComplete(UUID id, int status, String reason, long duration, Header[] responseHeaders,
			byte[] data);
	
	public void error(UUID id, String message, Exception e);
}
