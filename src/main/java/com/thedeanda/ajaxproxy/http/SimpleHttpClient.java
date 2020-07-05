package com.thedeanda.ajaxproxy.http;

import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.UUID;

public class SimpleHttpClient {
	private static final Logger log = LoggerFactory
			.getLogger(SimpleHttpClient.class);
	private HttpClient client;

	public SimpleHttpClient() {
		client = new HttpClient();
	}

	public String getString(String url) {
		byte[] bytes = getBytes(url);
		if (bytes == null)
			return null;
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.warn(e.getMessage(), e);
		}
		return null;
	}

	public byte[] getBytes(String url) {
		final Container<byte[]> container = new Container<byte[]>();
		byte[] nodata = new byte[]{};
		client.makeRequest(RequestMethod.GET, url, null, nodata,
				new RequestListener() {
					@Override
					public void newRequest(UUID id, String url, String method) {
					}

					@Override
					public void startRequest(UUID id, URL url,
							Header[] requestHeaders, byte[] data) {
					}

					@Override
					public void requestComplete(UUID id, int status,
							String reason, long duration,
							Header[] responseHeaders, byte[] data) {
						container.data = data;
					}

					@Override
					public void error(UUID id, String message, Exception e) {
						// TODO: throw exception
						log.warn("failed: {}", message, e);
					}

				});

		return container.data;
	}

	class Container<T> {
		public T data;
	}
}
