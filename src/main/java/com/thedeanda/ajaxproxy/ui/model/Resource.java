package com.thedeanda.ajaxproxy.ui.model;

import java.net.URL;
import java.util.UUID;

import org.apache.http.Header;

import com.thedeanda.ajaxproxy.LoadedResource;

public class Resource {
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
	

	public Resource(LoadedResource lr) {
		loadedResource = lr;
	}

	public Resource(UUID id, String url, String method) {
		this.id = id;
		this.url = url;
		this.method = method;
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

}
