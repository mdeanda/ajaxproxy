package com.thedeanda.ajaxproxy;

import com.thedeanda.ajaxproxy.config.model.MergeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class MergeServlet extends HttpServlet {
	private static final Logger log = LoggerFactory
			.getLogger(MergeServlet.class);

	private static final long serialVersionUID = -1234617738529639393L;
	private File filePath;
	private long lastLoad = 0;
	private String cache = null;
	private long CACHE_LIFE = 10000;
	private MergeMode mode;

	private boolean minify;

	private String urlPath;

	public MergeServlet(String filePath, MergeMode mode, boolean minify,
			String urlPath) throws Exception {
		log.info("new merge servlet: " + filePath + " mode: " + mode
				+ " minify: " + minify);
		this.mode = mode;
		this.filePath = new File(filePath);
		this.minify = minify;
		this.urlPath = urlPath;
		if (!this.filePath.exists()) {
			throw new FileNotFoundException(this.filePath.getAbsolutePath());
		}

		getContent();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String content = "";
		try {
			content = getContent();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		response.setContentLength(content.length());
		response.setContentType(this.mode.getContentType());

		PrintWriter w = response.getWriter();
		w.println(content);
		w.close();
	}

	protected long getLastModified(HttpServletRequest request) {
		return System.currentTimeMillis();
	}

	public String getContent() throws Exception {
		long now = System.currentTimeMillis();
		if (now > lastLoad + CACHE_LIFE) {
			MergeCode mc = new MergeCode();
			mc.setFilePath(filePath);
			mc.setMinify(minify);
			mc.setMode(mode);
			lastLoad = now;
			cache = mc.mergeContents();
		}
		return cache;
	}

	public String getUrlPath() {
		return urlPath;
	}

}
