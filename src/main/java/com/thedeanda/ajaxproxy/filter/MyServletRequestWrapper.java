package com.thedeanda.ajaxproxy.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class MyServletRequestWrapper extends HttpServletRequestWrapper {

	private byte[] data;

	public MyServletRequestWrapper(HttpServletRequest request) {
		super(request);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			IOUtils.copy(request.getInputStream(), baos);
		} catch (IOException e) {
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
			}
		}

		this.data = baos.toByteArray();
	}

	@Override
	public ServletInputStream getInputStream() {
		return new MyServletInputStream(getClonedInputStream());
	}

	public ByteArrayInputStream getClonedInputStream() {
		return new ByteArrayInputStream(data);
	}
	
	@Override
	public String getQueryString() {
		return null;
	}
}
