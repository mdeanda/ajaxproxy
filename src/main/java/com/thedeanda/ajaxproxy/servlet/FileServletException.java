package com.thedeanda.ajaxproxy.servlet;

abstract public class FileServletException extends Exception {
	private static final long serialVersionUID = 1L;

	abstract public int getCode();
}
