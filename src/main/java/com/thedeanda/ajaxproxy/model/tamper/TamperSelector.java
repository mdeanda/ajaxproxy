package com.thedeanda.ajaxproxy.model.tamper;

public class TamperSelector {
	private String name;
	private String pathRegEx;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathRegEx() {
		return pathRegEx;
	}

	public void setPathRegEx(String pathRegEx) {
		this.pathRegEx = pathRegEx;
	}
}
