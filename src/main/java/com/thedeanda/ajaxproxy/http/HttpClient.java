package com.thedeanda.ajaxproxy.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.model.config.HttpHeader;
import com.thedeanda.ajaxproxy.ui.ConfigService;

public class HttpClient {
	private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

	private final CloseableHttpClient client;

	public enum RequestMethod {
		GET(false), POST(true), PUT(true), DELETE(false), HEAD(false), PATCH(
				true), OPTIONS(true), TRACE(true);

		private boolean acceptsPayload;

		private RequestMethod(boolean accepts) {
			this.acceptsPayload = accepts;
		}

		public boolean isAcceptsPayload() {
			return acceptsPayload;
		}
	}

	public HttpClient() {
		final RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(5000).setConnectionRequestTimeout(60000)
				.setSocketTimeout(60000).build();

		HostnameVerifier verifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				log.debug("verify {}", hostname);
				return true;
			}
		};
		SSLContext ctx = getSslContext();
		String version = ConfigService.get().getVersionString();
		client = HttpClientBuilder
				.create()
				.disableAuthCaching()
				.disableAutomaticRetries()
				.disableConnectionState()
				.disableContentCompression()
				.disableCookieManagement()
				.disableRedirectHandling()
				.setSSLHostnameVerifier(verifier)
				.setSslcontext(ctx)
				.setConnectionReuseStrategy(
						DefaultConnectionReuseStrategy.INSTANCE)
				.setDefaultRequestConfig(requestConfig)
				.setUserAgent("AjaxProxy/" + version).build();

	}

	private SSLContext getSslContext() {
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0],
					new TrustManager[] { new X509TrustManager() {

						@Override
						public void checkClientTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							// TODO Auto-generated method stub

						}

						@Override
						public void checkServerTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							// TODO Auto-generated method stub

						}

						@Override
						public X509Certificate[] getAcceptedIssuers() {
							// TODO Auto-generated method stub
							return null;
						}
					} }, new SecureRandom());
			SSLContext.setDefault(ctx);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
		return ctx;
	}

	private void fireNewRequest(UUID uuid, String url, String method,
			RequestListener... listeners) {
		if (listeners != null) {
			for (RequestListener listener : listeners) {
				listener.newRequest(uuid, url, method);
			}
		}
	}

	private void fireError(UUID id, String message, Exception e,
			RequestListener... listeners) {
		if (listeners != null) {
			for (RequestListener listener : listeners) {
				listener.error(id, message, e);
			}
		}
	}

	private void fireStartRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data, RequestListener... listeners) {
		if (listeners != null) {
			for (RequestListener listener : listeners) {
				listener.startRequest(id, url, requestHeaders, data);
			}
		}
	}

	private void fireRequestComplete(UUID id, int status, String reason,
			long duation, Header[] responseHeaders, byte[] data,
			RequestListener... listeners) {
		if (listeners != null) {
			for (RequestListener listener : listeners) {
				listener.requestComplete(id, status, reason, duation,
						responseHeaders, data);
			}
		}
	}

	public void makeRequest(RequestMethod method, String url, List<HttpHeader> headers,
			byte[] input, RequestListener... listener) {

		UUID uuid = UUID.randomUUID();
		URL urlobj = null;
		// fireNewRequest(uuid, url, method.name(), listener);
		try {
			urlobj = new URL(url);
		} catch (MalformedURLException e) {
			fireError(uuid, e.getMessage(), e, listener);
		}

		Map<String, String> hds = new HashMap<>();
		if (headers !=null && !headers.isEmpty()) {
			for (HttpHeader line : headers) {
				hds.put(line.getName(), line.getValue());
			}
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

		makeRequest(method, urlobj, requestHeaders, input, listener);
	}

	public void makeRequest(RequestMethod method, URL url, Header[] headers,
			byte[] input, RequestListener... listener) {

		UUID uuid = UUID.randomUUID();
		fireNewRequest(uuid, url.toString(), method.toString(), listener);
		makeRequest_internal(uuid, method, url, headers, input, listener);
	}

	private void makeRequest_internal(UUID uuid, RequestMethod method, URL url,
			Header[] headers, byte[] input, RequestListener... listener) {

		fireStartRequest(uuid, url, headers, input, listener);

		makeRequestInternal(method, uuid, url, headers, input, listener);
	}

	private void makeRequestInternal(RequestMethod method, UUID id, URL url,
			Header[] requestHeaders, byte[] data, RequestListener... listener) {

		HttpRequestBase request = null;
		try {
			String target = url.toString();
			HttpEntity requestBody = new ByteArrayEntity(data,
					ContentType.APPLICATION_OCTET_STREAM);
			switch (method) {
			case GET:
				request = new HttpGet(target);
				break;
			case POST:
				HttpPost post = new HttpPost(target);
				post.setEntity(requestBody);
				request = post;
				break;
			case PUT:
				HttpPut put = new HttpPut(target);
				put.setEntity(requestBody);
				request = put;
				break;
			case DELETE:
				HttpDelete del = new HttpDelete(target);
				request = del;
				break;
			case HEAD:
				HttpHead head = new HttpHead(target);
				request = head;
				break;
			case PATCH:
				HttpPatch patch = new HttpPatch(target);
				patch.setEntity(requestBody);
				request = patch;
				break;
			}
			request.setHeaders(requestHeaders);
			log.trace(">> Request URI: " + request.getRequestLine().getUri());

			long start = System.currentTimeMillis();
			HttpResponse response = client.execute(request);
			long end = System.currentTimeMillis();
			StatusLine status = response.getStatusLine();
			log.trace("<< Response: " + response.getStatusLine());

			byte[] bytes = null;
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				bytes = EntityUtils.toByteArray(response.getEntity());
			}
			Header[] headers = response.getAllHeaders();
			fireRequestComplete(id, status.getStatusCode(),
					status.getReasonPhrase(), (end - start), headers, bytes,
					listener);

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fireError(id, e.getMessage(), e, listener);
		} finally {
			if (request != null) {
				request.releaseConnection();
			}
		}
	}

}
