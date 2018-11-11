package com.thedeanda.ajaxproxy.http;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NetworkUtilTest {
	private byte[] readBytes(String filename) throws IOException {
		FileInputStream is = new FileInputStream("src/test/resources/" + filename);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtils.copy(is, os);
		return os.toByteArray();
	}

	private void verifyBytes(byte[] a, byte[] b) {
		assertEquals(a.length, b.length);
		for (int i = 0; i < a.length; i++) {
			assertEquals(a[i], b[i]);
		}
	}

	@Test
	public void testNoDecompression() throws IOException {
		byte[] input = readBytes("sample.txt");
		byte[] expected = readBytes("sample.txt");

		assertNotNull(input);

		byte[] output = NetworkUtil.decompress("", input);

		assertNotNull(output);
		assertNotNull(expected);
		verifyBytes(expected, output);
	}

	@Test
	public void testDecompressionGzip() throws IOException {
		byte[] input = readBytes("sample.gz");
		byte[] expected = readBytes("sample.txt");

		assertNotNull(input);

		byte[] output = NetworkUtil.decompress("gzip", input);

		assertNotNull(output);
		assertNotNull(expected);
		verifyBytes(expected, output);
	}

	@Test
	public void testDecompressionLzma() throws IOException {
		byte[] input = readBytes("sample.lzma");
		byte[] expected = readBytes("sample.txt");

		assertNotNull(input);

		byte[] output = NetworkUtil.decompress("lzma", input);

		assertNotNull(output);
		assertNotNull(expected);
		verifyBytes(expected, output);
	}

	@Test
	public void testDecompressionDeflate() throws IOException {
		byte[] input = readBytes("sample.zlib");
		byte[] expected = readBytes("sample.txt");

		assertNotNull(input);

		byte[] output = NetworkUtil.decompress("deflate", input);

		assertNotNull(output);
		assertNotNull(expected);
		verifyBytes(expected, output);
	}
}
