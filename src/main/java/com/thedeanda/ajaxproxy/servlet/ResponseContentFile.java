package com.thedeanda.ajaxproxy.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ResponseContentFile implements ResponseContent {
	private static final Logger log = LoggerFactory.getLogger(ResponseContentFile.class);
	private File file;
	private String contentType;

	public ResponseContentFile(File file) {
		this.file = file;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public String getEtag() {
		// TODO: make some kind of checksum out of this as its not secure!
		return file.getAbsolutePath();
	}

	@Override
	public String getFilename() {
		return file.getName();
	}

	@Override
	public long getLength() {
		return file.length();
	}

	@Override
	public long getLastModified() {
		return file.lastModified();
	}

	@Override
	public boolean isDownload() {
		return false;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean isStreamingOnly() {
		return false;
	}

	@Override
	public InputStream getInputStream() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public RandomAccessFile getRandomAccessFile() {
		try {
			return new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
			return null;
		}
	}

}
