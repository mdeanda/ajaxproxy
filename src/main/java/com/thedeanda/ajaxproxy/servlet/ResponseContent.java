package com.thedeanda.ajaxproxy.servlet;

import java.io.InputStream;
import java.io.RandomAccessFile;

public interface ResponseContent {
	public boolean exists();

	public String getEtag();

	public String getFilename();

	public long getLength();

	public long getLastModified();

	public boolean isDownload();

	public String getContentType();

	public void setContentType(String contentType);

	/**
	 * if streaming only, then only getInputStream gets used, otherwise
	 * getRandomAccessFile might be used
	 */
	public boolean isStreamingOnly();

	public InputStream getInputStream();

	public RandomAccessFile getRandomAccessFile();
}
