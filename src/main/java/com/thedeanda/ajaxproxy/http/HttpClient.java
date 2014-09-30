package com.thedeanda.ajaxproxy.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
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

	public HttpClient() {
		httpproc = HttpProcessorBuilder.create().add(new RequestContent())
				.add(new RequestTargetHost()).add(new RequestConnControl())
				.add(new RequestUserAgent("AjaxProxy/1.1"))
				.add(new RequestExpectContinue(true)).build();

		httpexecutor = new HttpRequestExecutor();

	}

	public void makeRequest(String method, String url, String headers,
			String input, RequestListener listener)
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
			res.setHeaders(hds);
		}

		res.setMethod(method);

		int port = urlobj.getPort();
		if (port <= 0) {
			port = 80;
		}
		replay(urlobj.getHost(), port, res, listener);
	}

	public void replay(String host, int port, LoadedResource resource,
			RequestListener listener) {
		if ("GET".equalsIgnoreCase(resource.getMethod())) {
			replayGet(host, port, resource, listener);
		} else if ("POST".equalsIgnoreCase(resource.getMethod())) {
			replayPost(host, port, resource, listener);
		}
	}

	private void addHeaders(BasicHttpRequest request, LoadedResource resource) {
		Map<String, String> headers = resource.getHeaders();
		ArrayList<Header> xheaders = new ArrayList<Header>();
		for (String key : headers.keySet()) {
			if (!"Content-Length".equalsIgnoreCase(key)) {
				Header hdr = new BasicHeader(key, headers.get(key));
				xheaders.add(hdr);
			}
		}
		Header[] val = new Header[xheaders.size()];
		xheaders.toArray(val);
		request.setHeaders(val);
	}

	private void replayGet(String hostname, int port, LoadedResource resource,
			RequestListener listener) {
		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(hostname, port);
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(
				8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
		try {
			String target = resource.getPath();
			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket);
			}
			BasicHttpRequest request = new BasicHttpRequest("GET", target);
			addHeaders(request, resource);
			log.trace(">> Request URI: " + request.getRequestLine().getUri());

			httpexecutor.preProcess(request, httpproc, coreContext);
			HttpResponse response = httpexecutor.execute(request, conn,
					coreContext);
			httpexecutor.postProcess(response, httpproc, coreContext);

			log.trace("<< Response: " + response.getStatusLine());
			if (listener!=null) {
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				listener.requestComplete(200, response.getAllHeaders(), bytes);
			}
			log.trace("==============");
			if (!connStrategy.keepAlive(response, coreContext)) {
				conn.close();
			} else {
				log.debug("Connection kept alive...");
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

	private void replayPost(String hostname, int port, LoadedResource resource,
			RequestListener listener) {
		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(hostname, port);
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(
				8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
		try {
			String target = resource.getPath();
			HttpEntity requestBody = new ByteArrayEntity(resource.getInput(),
					ContentType.APPLICATION_OCTET_STREAM);

			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket);
			}
			BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
					"POST", target);
			addHeaders(request, resource);
			request.setEntity(requestBody);
			log.trace(">> Request URI: " + request.getRequestLine().getUri());

			httpexecutor.preProcess(request, httpproc, coreContext);
			HttpResponse response = httpexecutor.execute(request, conn,
					coreContext);
			httpexecutor.postProcess(response, httpproc, coreContext);

			log.trace("<< Response: " + response.getStatusLine());
			log.trace(EntityUtils.toString(response.getEntity()));
			log.trace("==============");
			if (!connStrategy.keepAlive(response, coreContext)) {
				conn.close();
			} else {
				log.debug("Connection kept alive...");
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
