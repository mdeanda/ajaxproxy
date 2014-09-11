package com.thedeanda.ajaxproxy.http;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

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

	public void replay(String host, int port, LoadedResource resource) {
		if ("GET".equalsIgnoreCase(resource.getMethod())) {
			replayGet(host, port, resource);
		} else if ("POST".equalsIgnoreCase(resource.getMethod())) {
			replayPost(host, port, resource);
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

	private void replayGet(String hostname, int port, LoadedResource resource) {
		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(hostname, port);
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(
				8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
		try {
			String target = resource.getUrl();
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

	private void replayPost(String hostname, int port, LoadedResource resource) {
		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(hostname, port);
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(
				8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
		try {
			String target = resource.getUrl();
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
