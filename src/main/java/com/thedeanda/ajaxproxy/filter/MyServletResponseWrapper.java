package com.thedeanda.ajaxproxy.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public class MyServletResponseWrapper implements HttpServletResponse {
	private ByteArrayOutputStream baos;
	private PrintWriter writer;
	private MyServletOutputStream os;
	private int httpStatus = 200;
	private HttpServletResponseWrapper impl;

	public MyServletResponseWrapper(HttpServletResponse response) {
		impl = new HttpServletResponseWrapper(response);

		baos = new ByteArrayOutputStream();
		writer = new PrintWriter(baos);
		os = new MyServletOutputStream(baos);
	}

	@Override
	public void flushBuffer() throws IOException {
		impl.flushBuffer();
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
		impl.reset();
		this.httpStatus = SC_OK;
	}

	@Override
	public void sendError(int sc) throws IOException {
		httpStatus = sc;
		impl.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		httpStatus = 302;
		impl.sendRedirect(location);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		httpStatus = sc;
		impl.sendError(sc, msg);
	}

	@Override
	public void setStatus(int sc) {
		httpStatus = sc;
		impl.setStatus(sc);
	}

	public int getStatus() {
		return httpStatus;
	}

	@Override
	public String getCharacterEncoding() {
		return impl.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return impl.getContentType();
	}

	@Override
	public void setCharacterEncoding(String charset) {
		impl.setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len) {
		impl.setContentLength(len);
	}

	@Override
	public void setContentType(String type) {
		impl.setContentType(type);
	}

	@Override
	public void setBufferSize(int size) {
		impl.setBufferSize(size);
	}

	@Override
	public int getBufferSize() {
		return impl.getBufferSize();
	}

	@Override
	public void resetBuffer() {
		impl.resetBuffer();
	}

	@Override
	public boolean isCommitted() {
		return impl.isCommitted();
	}

	@Override
	public void setLocale(Locale loc) {
		impl.setLocale(loc);
	}

	@Override
	public Locale getLocale() {
		return impl.getLocale();
	}

	@Override
	public void addCookie(Cookie cookie) {
		impl.addCookie(cookie);
	}

	@Override
	public boolean containsHeader(String name) {
		return impl.containsHeader(name);
	}

	@Override
	public String encodeURL(String url) {
		return impl.encodeUrl(url);
	}

	@Override
	public String encodeRedirectURL(String url) {
		return impl.encodeRedirectURL(url);
	}

	@Override
	public String encodeUrl(String url) {
		return impl.encodeUrl(url);
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return impl.encodeRedirectUrl(url);
	}

	@Override
	public void setDateHeader(String name, long date) {
		impl.setDateHeader(name, date);
	}

	@Override
	public void addDateHeader(String name, long date) {
		impl.addDateHeader(name, date);
	}

	@Override
	public void setHeader(String name, String value) {
		impl.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		impl.addHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		impl.setIntHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		impl.addIntHeader(name, value);
	}

	@Override
	public void setStatus(int sc, String sm) {
		impl.setStatus(sc, sm);
	}

	@Override
	public void setContentLengthLong(long len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}
}