package com.thedeanda.ajaxproxy.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtil {
	private static final Logger log = LoggerFactory
			.getLogger(NetworkUtil.class);
	private static final String CONTENT_ENCODING = "Content-Encoding";

	public static byte[] decompressIfNeeded(byte[] bytes, Header[] headers) {
		boolean gzip = false;
		for (Header h : headers) {
			if (CONTENT_ENCODING.equals(h.getName())) {
				if ("gzip".equals(h.getValue())) {
					gzip = true;
				}
			}
		}

		if (gzip) {
			try {
				InputStream is = new GZIPInputStream(new ByteArrayInputStream(
						bytes));
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				IOUtils.copy(is, output);
				bytes = output.toByteArray();
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		}

		return bytes;
	}

}
