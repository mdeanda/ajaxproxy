package com.thedeanda.ajaxproxy.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyServletResponseWrapper extends HttpServletResponseWrapper {
	private static final Logger log = LoggerFactory
			.getLogger(MyServletResponseWrapper.class);
	private ByteArrayOutputStream baos;
	private PrintWriter writer;
	private MyServletOutputStream os;
	private int httpStatus = 200;

	public MyServletResponseWrapper(HttpServletResponse response) {
		super(response);

		baos = new ByteArrayOutputStream();
		writer = new PrintWriter(baos);
		os = new MyServletOutputStream(baos);
	}

	@Override
	public void flushBuffer() {

	}

	@Override
	public ServletOutputStream getOutputStream() {
		return os;
	}

	@Override
	public PrintWriter getWriter() {
		return writer;
	}

	public ByteArrayInputStream getNewInputStream() {
		ByteArrayInputStream ret = new ByteArrayInputStream(baos.toByteArray());
		return ret;
	}

	@Override
	public void reset() {
		super.reset();
		this.httpStatus = SC_OK;
	}

	@Override
	public void sendError(int sc) throws IOException {
		httpStatus = sc;
		super.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		httpStatus = 302;
		super.sendRedirect(location);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		httpStatus = sc;
		super.sendError(sc, msg);
	}

	@Override
	public void setStatus(int sc) {
		httpStatus = sc;
		super.setStatus(sc);
	}

	public int getStatus() {
		return httpStatus;
	}
}