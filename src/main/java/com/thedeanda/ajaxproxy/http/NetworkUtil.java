package com.thedeanda.ajaxproxy.http;

import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class NetworkUtil {
	private static final Logger log = LoggerFactory.getLogger(NetworkUtil.class);
	private static final String CONTENT_ENCODING = "Content-Encoding";
	private static final String GZIP = "gzip";
	private static final String LZMA = "lzma";
	private static final String DEFLATE = "deflate";

	public static byte[] decompressIfNeeded(byte[] bytes, Header[] headers) {
		String encoding = getContentEncoding(headers);

		return decompress(encoding, bytes);
	}

	public static String getContentEncoding(Header[] headers) {
		String encoding = null;

		for (Header h : headers) {
			if (CONTENT_ENCODING.equals(h.getName())) {
				encoding = h.getValue();
				break;
			}
		}

		return encoding;
	}

	public static byte[] decompress(String encoding, byte[] bytes) {

		if (GZIP.equalsIgnoreCase(encoding)) {
			try {
				InputStream is = new GZIPInputStream(new ByteArrayInputStream(bytes));
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				IOUtils.copy(is, output);
				bytes = output.toByteArray();
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		} else if (LZMA.equalsIgnoreCase(encoding)) {
			try {
				LzmaInputStream inputStream = new LzmaInputStream(
						new BufferedInputStream(new ByteArrayInputStream(bytes)), new Decoder());
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				IOUtils.copy(inputStream, output);
				bytes = output.toByteArray();
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		} else if (DEFLATE.equalsIgnoreCase(encoding)) {
			try {
				Inflater decompresser = new Inflater();
				decompresser.setInput(bytes, 0, bytes.length);
				byte[] result = new byte[decompresser.getRemaining()];
				int resultLength = decompresser.inflate(result);
				decompresser.end();
				bytes = Arrays.copyOf(result, resultLength);
			} catch (DataFormatException e) {
				log.warn(e.getMessage(), e);
			}
		} //TODO: LZW ("compress") format

		return bytes;
	}

}
