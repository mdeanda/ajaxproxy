package com.thedeanda.ajaxproxy.ui.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resource implements Serializable, Comparable<Resource> {
	private static final long serialVersionUID = -2666007600337135608L;
	private static final Logger log = LoggerFactory.getLogger(Resource.class);

	private UUID id;
	private String url;
	private String method;

	private URL urlObject;
	private Header[] requestHeaders;

	private int status;
	private String reason;
	private long duration;
	private Header[] responseHeaders;

	private String errorReason;
	private String exception;
	private long startTime = System.currentTimeMillis();

	/**
	 * calculated fields
	 */
	private String path;

	public Resource(UUID id, String url, String method) {
		this.id = id;
		this.url = url;
		this.method = method;

		// TODO: calculate path from url
		try {
			URL urlObject = new URL(url);
			setPath(urlObject.getPath());
		} catch (MalformedURLException e) {
			log.warn(e.getMessage(), e);
		} finally {

		}
	}

	@Override
	public int compareTo(Resource o) {
		return (int) (getStartTime() - o.getStartTime());
	}

	public UUID getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getMethod() {
		return method;
	}

	public URL getUrlObject() {
		return urlObject;
	}

	public void setUrlObject(URL urlObject) {
		this.urlObject = urlObject;
	}

	public Header[] getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Header[] requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Header[] responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
