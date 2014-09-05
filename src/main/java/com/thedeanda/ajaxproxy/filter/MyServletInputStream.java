package com.thedeanda.ajaxproxy.filter;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

public class MyServletInputStream extends ServletInputStream {
	private InputStream is;

	public MyServletInputStream(InputStream is) {
		this.is = is;
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}
}