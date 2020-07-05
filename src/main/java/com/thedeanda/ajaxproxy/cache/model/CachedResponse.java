package com.thedeanda.ajaxproxy.cache.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

@DatabaseTable(tableName = "cache")
public class CachedResponse {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(width = 2048)
	private String requestPath;

	@DatabaseField(dataType = DataType.LONG)
	private long timestamp = System.currentTimeMillis();

	@DatabaseField(width = 2048)
	private String url;

	@DatabaseField(width = 2048)
	private String queryString;

	@DatabaseField
	private int status;

	@DatabaseField
	private String reason;

	@DatabaseField(dataType = DataType.SERIALIZABLE, width = 1024 * 1024)
	private byte[] data;

	private Header[] headers;

	public static String getFullUrl(String requstUrl, String queryString) {
		if (StringUtils.isBlank(queryString)) {
			return requstUrl;
		} else {
			return requstUrl + "?" + queryString;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

}
