package com.thedeanda.ajaxproxy.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class MyServletOutputStream extends ServletOutputStream {
	private OutputStream os;

	public MyServletOutputStream(OutputStream os) {
		this.os = os;
	}

	@Override
	public void write(int bite) throws IOException {
		os.write(bite);
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		// TODO Auto-generated method stub
		
	}
}
