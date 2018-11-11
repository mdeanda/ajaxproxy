package com.thedeanda.ajaxproxy;

import com.thedeanda.ajaxproxy.config.ConfigService;
import com.thedeanda.ajaxproxy.http.EmptyRequestListener;
import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import org.apache.http.Header;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * launches ajax proxy and confirms that headers match when making request
 * directly to website and when proxied
 * 
 * @author mdeanda
 *
 */
public class TestHeaders {
	private static final String REAL_HOST = "losangeles.craigslist.org";
	private static final String REAL_URL = "http://losangeles.craigslist.org/";
	private static final int PORT = 8888;
	private static final String PROXY_URL = "http://localhost:8888/";
	private HttpClient client;

	@Before
	public void init() {
		client = new HttpClient();
	}

	@Ignore
	@Test
	public void testHeadersNewProxy() throws Exception {
		JsonObject config = new JsonObject();
		config.put(AjaxProxy.RESOURCE_BASE, ".");
		config.put(AjaxProxy.PORT, 8888);
		JsonArray proxyArray = new JsonArray();
		config.put(AjaxProxy.PROXY_ARRAY, proxyArray);
		JsonObject proxyPath = new JsonObject();
		proxyArray.add(proxyPath);
		proxyPath.put(AjaxProxy.DOMAIN, REAL_HOST);
		proxyPath.put(AjaxProxy.PATH, ".*");
		proxyPath.put(AjaxProxy.PORT, 80);

		ConfigService configService = new ConfigService();
		AjaxProxy proxy = new AjaxProxy(config, new File("."), new EmptyRequestListener(), configService);
		new Thread(proxy).start();

		Response realResponse = getResponse(REAL_URL);
		assertNotNull(realResponse);

		Response proxyResponse = getResponse(PROXY_URL);
		assertNotNull(proxyResponse);

		String data1 = new String(realResponse.data);
		String data2 = new String(proxyResponse.data);
		
		assertEquals("status mismatch", realResponse.status,
				proxyResponse.status);
		assertEquals("data strings", data1, data2);
		assertEquals("data length mismatch", realResponse.data.length,
				proxyResponse.data.length);
		assertEquals("header length mismatch", realResponse.headers.length,
				proxyResponse.headers.length);
	}

	private Response getResponse(final String url) throws InterruptedException {
		final Response response = new Response();
		final ReentrantLock lock = new ReentrantLock();
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				client.makeRequest(RequestMethod.GET, url, null, new byte[] {},
						new RequestListener() {

							@Override
							public void newRequest(UUID id, String url,
									String method) {

							}

							@Override
							public void startRequest(UUID id, URL url,
									Header[] requestHeaders, byte[] data) {

							}

							@Override
							public void requestComplete(UUID id, int status,
									String reason, long duration,
									Header[] responseHeaders, byte[] data) {
								response.status = status;
								response.headers = responseHeaders;
								response.data = data;
								lock.unlock();
							}

							@Override
							public void error(UUID id, String message,
									Exception e) {
								lock.unlock();
							}

						});
			}

		}).start();

		Thread.sleep(500);
		lock.lock();
		return response;

	}

	private static class Response {
		public int status;
		public byte[] data;
		public Header[] headers;
	}
}
