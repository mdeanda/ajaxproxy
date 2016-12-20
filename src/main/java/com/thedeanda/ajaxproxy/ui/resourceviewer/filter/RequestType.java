package com.thedeanda.ajaxproxy.ui.resourceviewer.filter;

public enum RequestType {
	GET(true, false), POST(true, false), PUT(true, false), DELETE(true, false), HEAD(true, false), PATCH(true,
			false), OPTIONS(true, false), TRACE(true,
					false), STATUS_200S(false, true), STATUS_400S(false, true), STATUS_600S(false, true);

	private boolean method;
	private boolean response;

	private RequestType(boolean method, boolean response) {
		this.method = method;
		this.response = response;
	}

	public boolean isMethod() {
		return method;
	}

	public boolean isResponse() {
		return response;
	}
}
