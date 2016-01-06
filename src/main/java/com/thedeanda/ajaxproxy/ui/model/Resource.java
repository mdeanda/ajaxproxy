package com.thedeanda.ajaxproxy.ui.model;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.Header;
import org.mortbay.log.Log;

import com.thedeanda.ajaxproxy.LoadedResource;

public class Resource implements Serializable, Comparable<Resource> {
	private static final long serialVersionUID = -2666007600337135608L;

	/**
	 * old stuff, should go away if new proxy works
	 */
	private LoadedResource loadedResource;

	private UUID id;
	private String url;
	private String method;

	private URL urlObject;
	private Header[] requestHeaders;
	private byte[] inputData;

	private int status;
	private String reason;
	private long duration;
	private Header[] responseHeaders;
	private byte[] outputData;

	private String errorReason;
	private String exception;
	private long startTime = System.currentTimeMillis();

	/**
	 * calculated fields
	 */
	private String path;

	public Resource(LoadedResource lr) {
		loadedResource = lr;

		setUrl(loadedResource.getUrl());
		setPath(lr.getPath());
	}

	public Resource(UUID id, String url, String method) {
		this.id = id;
		this.url = url;
		this.method = method;

		// TODO: calculate path from url
		try {
			URL urlObject = new URL(url);
			setPath(urlObject.getPath());
		} catch (MalformedURLException e) {
			Log.warn(e.getMessage(), e);
		} finally {

		}
	}

	@Override
	public int compareTo(Resource o) {
		return (int) (getStartTime() - o.getStartTime());
	}

	public LoadedResource getLoadedResource() {
		return loadedResource;
	}

	public void setLoadedResource(LoadedResource loadedResource) {
		this.loadedResource = loadedResource;
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

	public byte[] getInputData() {
		return inputData;
	}

	public void setInputData(byte[] inputData) {
		this.inputData = inputData;
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

	public byte[] getOutputData() {
		return outputData;
	}

	public void setOutputData(byte[] outputData) {
		this.outputData = outputData;
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
		if (this.loadedResource != null) {
			return this.loadedResource.getDate().getTime();
		} else {
			return startTime;
		}
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
