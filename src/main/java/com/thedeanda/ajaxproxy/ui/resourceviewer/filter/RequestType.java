package com.thedeanda.ajaxproxy.ui.resourceviewer.filter;

public enum RequestType {
	GET(true, false), POST(true, false), PUT(true, false), DELETE(true, false), 
			HEAD(true, false), PATCH(true, false), OPTIONS(true, false), TRACE(true, false), 
			STATUS_1xx(false, true, 1), STATUS_2xx(false, true, 2), STATUS_3xx(false, true, 3),
			STATUS_4xx(false, true, 4), STATUS_5xx(false, true, 5);

	private boolean method;
	private boolean response;
	private int status = 0;

	private RequestType(boolean method, boolean response) {
		this(method, response, 0);
	}
	
	private RequestType(boolean method, boolean response, int status) {
		this.method = method;
		this.response = response;
		this.status = status;
	}

	public boolean isMethod() {
		return method;
	}

	public boolean isStatusCode() {
		return response;
	}
	
	public boolean isStatusInRange(int status) {
		boolean ret = false;
		if (status > 0) {
			ret = (status / 100) == this.status;
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return name().replace("_", ": ");
	}
}
