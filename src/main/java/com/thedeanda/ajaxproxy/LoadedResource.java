package com.thedeanda.ajaxproxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.Cookie;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;

import com.thedeanda.ajaxproxy.http.RequestListener;

public class LoadedResource implements RequestListener {
	private UUID id;
	private String url;
	private long duration;
	private byte[] input = new byte[0];
	private byte[] output;
	private int statusCode;
	private String statusMessage;
	private String method;
	private Header[] responseHeaders;
	private Header[] requestHeaders;
	private Date date;

	private String path;
	private List<Cookie> cookies;
	private String characterEncoding;
	private Exception filterException;

	transient private String inputAsText;
	transient private String outputAsText;

	@Override
	public String toString() {
		return path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public byte[] getInput() {
		return input;
	}

	public void setInput(byte[] input) {
		this.input = input;
	}

	public byte[] getOutput() {
		return output;
	}

	public void setOutput(byte[] output) {
		this.output = output;
	}

	public String getOutputAsText() {
		if (outputAsText == null) {
			outputAsText = convertToText(output);
		}
		return outputAsText;
	}

	public String getInputAsText() {
		if (inputAsText == null) {
			inputAsText = convertToText(input);
		}
		return inputAsText;
	}

	private String convertToText(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		if (bytes != null) {
			try {
				@SuppressWarnings("unchecked")
				List<String> lines = IOUtils
						.readLines(new ByteArrayInputStream(bytes));
				for (String line : lines) {
					sb.append(line);
					sb.append("\n");
				}
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

	public Header[] getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Header[] headers) {
		this.requestHeaders = headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public Exception getFilterException() {
		return filterException;
	}

	public void setFilterException(Exception filterException) {
		this.filterException = filterException;
	}

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	@Override
	public void newRequest(UUID id, String url, String method) {
		this.id = id;
		this.url = url;
		this.method = method;
		this.date = new Date();
	}

	@Override
	public void startRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {
		this.input = data;
		this.requestHeaders = requestHeaders;
	}

	@Override
	public void requestComplete(UUID id, int status, String reason,
			long duration, Header[] responseHeaders, byte[] data) {
		this.statusCode = status;
		this.statusMessage = reason;
		this.duration = duration;
		this.output = data;
		this.responseHeaders = responseHeaders;
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		// TODO Auto-generated method stub

	}

	public String getStatusMessage() {
		return statusMessage;
	}
}
