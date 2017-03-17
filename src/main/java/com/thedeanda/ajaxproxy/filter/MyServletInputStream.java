package com.thedeanda.ajaxproxy.filter;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
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

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		// TODO Auto-generated method stub
		
	}
}