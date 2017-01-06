package com.thedeanda.ajaxproxy.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		// TODO: make some kind of checksum out of this
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
