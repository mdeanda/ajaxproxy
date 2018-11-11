package com.thedeanda.ajaxproxy.http;

import org.apache.http.Header;

import java.net.URL;
import java.util.UUID;

public class EmptyRequestListener implements RequestListener {

	@Override
	public void newRequest(UUID id, String url, String method) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRequest(UUID id, URL url, Header[] requestHeaders, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestComplete(UUID id, int status, String reason, long duration, Header[] responseHeaders,
			byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		// TODO Auto-generated method stub
		
	}

}
