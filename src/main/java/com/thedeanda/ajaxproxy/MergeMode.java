package com.thedeanda.ajaxproxy;

public enum MergeMode {
	JS("text/javascript"), CSS("text/css"), HTML_JSON("text/plain"), PLAIN(
			"text/plain");

	private String contentType;

	MergeMode(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}
}
