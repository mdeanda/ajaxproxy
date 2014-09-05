package com.thedeanda.ajaxproxy.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

public class MyServletOutputStream extends ServletOutputStream {
	private OutputStream os;

	public MyServletOutputStream(OutputStream os) {
		this.os = os;
	}

	@Override
	public void write(int bite) throws IOException {
		os.write(bite);
	}
}
