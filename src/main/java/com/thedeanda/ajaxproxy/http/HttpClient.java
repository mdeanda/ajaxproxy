package com.thedeanda.ajaxproxy.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.LoadedResource;

public class HttpClient {
	private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
	HttpRequestExecutor httpexecutor;
	HttpProcessor httpproc;

	public enum RequestMethod {
		GET, POST, PUT
	}

	public HttpClient() {
		httpproc = HttpProcessorBuilder.create().add(new RequestContent())
				.add(new RequestTargetHost()).add(new RequestConnControl())
				.add(new RequestUserAgent("AjaxProxy/1.1"))
				.add(new RequestExpectContinue(true)).build();

		httpexecutor = new HttpRequestExecutor();

	}

	public void makeRequest(RequestMethod method, String url, String headers,
			byte[] input, RequestListener listener)
			throws MalformedURLException {
		URL urlobj = new URL(url);
		LoadedResource res = new LoadedResource();

		String query = urlobj.getQuery();
		String requestPath = urlobj.getPath();
		if (!StringUtils.isBlank(query)) {
			requestPath += "?" + query;
		}
		res.setPath(requestPath);

		Map<String, String> hds = new HashMap<>();
		if (!StringUtils.isBlank(headers)) {
			String[] lines = StringUtils.split(headers, "\n");
			for (String line : lines) {
				String[] parts = StringUtils.split(line, ":", 2);
				hds.put(parts[0], parts[1]);
			}
			res.setRequestHeaders(hds);
		}
		Header[] requestHeaders = null;
		if (hds.size() > 0) {
			requestHeaders = new Header[hds.size()];
			int i = 0;
			for (String key : hds.keySet()) {
				Header h = new BasicHeader(key, hds.get(key));
				requestHeaders[i++] = h;
			}
		}

		res.setMethod(method.name());

		int port = urlobj.getPort();
		if (port <= 0) {
			port = 80;
		}

		UUID uuid = UUID.randomUUID();
		if (listener != null) {
			listener.newRequest(uuid, urlobj, requestHeaders, input);
		}

		makeRequestInternal(method, uuid, urlobj, requestHeaders, input,
				listener);

	}

	public void replay(String host, int port, LoadedResource resource,
			RequestListener listener) {
		// TODO: map to makeRequest params
		UUID id = UUID.randomUUID();
		if ("GET".equalsIgnoreCase(resource.getMethod())) {
			// doGet(id, host, port, resource, listener);
		} else if ("POST".equalsIgnoreCase(resource.getMethod())) {
			// doPost(host, port, resource, listener);
		}
	}

	private HttpHost getHost(URL url) {
		int port = url.getPort();
		if (port < 0) {
			port = 80;
		}
		HttpHost host = new HttpHost(url.getHost(), port);
		return host;
	}

	private void makeRequestInternal(RequestMethod method, UUID id, URL url,
			Header[] requestHeaders, byte[] data, RequestListener listener) {
		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = getHost(url);
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(
				8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
		try {
			String target = url.getPath();
			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket);
			}
			HttpRequestBase request = null;
			HttpEntity requestBody;
			switch (method) {
			case GET:
				request = new HttpGet(target);
				break;
			case POST:
				requestBody = new ByteArrayEntity(data,
						ContentType.APPLICATION_OCTET_STREAM);
				HttpPost post = new HttpPost(target);
				post.setEntity(requestBody);
				request = post;
				break;
			case PUT:
				requestBody = new ByteArrayEntity(data,
						ContentType.APPLICATION_OCTET_STREAM);
				HttpPut put = new HttpPut();
				put.setEntity(requestBody);
				request = put;
				break;
			}
			request.setHeaders(requestHeaders);
			log.info(">> Request URI: " + request.getRequestLine().getUri());

			httpexecutor.preProcess(request, httpproc, coreContext);
			HttpResponse response = httpexecutor.execute(request, conn,
					coreContext);
			httpexecutor.postProcess(response, httpproc, coreContext);

			log.info("<< Response: " + response.getStatusLine());
			if (listener != null) {
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				listener.requestComplete(id, response.getStatusLine()
						.getStatusCode(), response.getAllHeaders(), bytes);
			}
			log.info("==============");
			if (!connStrategy.keepAlive(response, coreContext)) {
				conn.close();
			} else {
				log.info("Connection kept alive...");
			}
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (HttpException e) {
			log.warn(e.getMessage(), e);
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		}
	}
}
