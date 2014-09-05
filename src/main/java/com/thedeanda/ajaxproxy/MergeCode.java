package com.thedeanda.ajaxproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import net.sourceforge.javajson.JsonObject;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class MergeCode {
	private static final Logger log = LoggerFactory.getLogger(MergeCode.class);
	private File filePath;
	private boolean minify;
	private MergeMode mode;

	@SuppressWarnings("unchecked")
	private List<String> getFileList(File filePath) throws IOException {
		List<String> lines = FileUtils.readLines(filePath);
		return lines;
	}

	public MergeCode() {

	}

	/**
	 * 
	 * @param filePath
	 *            input file with lines that contain filenames to merge
	 * @param minify
	 * @param jsMode
	 *            true when minifying javascript, false for css
	 * @return
	 * @throws Exception
	 */
	public String mergeContents() throws Exception {
		StringBuffer sb = new StringBuffer();
		if (mode == MergeMode.HTML_JSON) {
			sb.append(mergeHtmlJson());
		} else {
			sb.append(mergeJsCssPlain());
		}
		return sb.toString();
	}

	private String mergeHtmlJson() throws Exception {
		JsonObject json = new JsonObject();
		String basePath = filePath.getParentFile().getAbsolutePath()
				+ File.separator;

		JsonObject input = null;
		InputStream is = new FileInputStream(filePath);
		try {
			input = JsonObject.parse(is);
		} finally {
			is.close();
		}

		for (String key : input) {
			String fname = basePath + input.getString(key);
			if (log.isTraceEnabled())
				log.trace("merging: " + fname);
			File f = new File(fname);
			if (f.isFile() && !f.isHidden()) {
				String fileData = FileUtils.readFileToString(f);
				json.put(key, fileData);
			} else {
				log.warn("not a readable file: " + f.getAbsolutePath());
			}
		}
		return json.toString(2);
	}

	private String mergeJsCssPlain() throws Exception {
		StringBuffer sb = new StringBuffer();
		String basePath = filePath.getParentFile().getAbsolutePath()
				+ File.separator;

		int totalLength = 0;
		for (String line : getFileList(filePath)) {
			if (log.isTraceEnabled())
				log.trace("merging: " + basePath + line);
			File f = new File(basePath + line);
			if (f.isFile() && !f.isHidden()) {
				String fileData = FileUtils.readFileToString(f);
				totalLength += fileData.length();
				if (minify) {
					try {
						if (mode == MergeMode.JS)
							fileData = minifyJsYahoo(fileData);
						else if (mode == MergeMode.CSS)
							fileData = minifyCssYahoo(fileData);
					} catch (Exception ex) {
						log.warn("can't minify: " + f, ex);
					}
				}
				if (mode == MergeMode.JS) {
					if (minify) {
						sb.append("// start of: ");
						sb.append(line);
						sb.append("\n");
					} else {
						sb.append("// start of: ");
						sb.append(basePath);
						sb.append(line);
						sb.append("\n");
					}
				}
				sb.append(fileData);
				sb.append("\n");
			} else {
				log.warn("not a readable file: " + f.getAbsolutePath());
			}
		}
		if (minify) {
			int merged = sb.length();
			log.info("minify code (" + filePath.getName() + "): " + totalLength
					+ "=>" + merged);
		}

		return sb.toString();
	}

	private static String minifyCssYahoo(String data) throws IOException {
		CssCompressor compressor = new CssCompressor(new StringReader(data));

		Writer out = new StringWriter();

		compressor.compress(out, 20000);
		return out.toString();
	}

	private static String minifyJsYahoo(String data) throws IOException {
		Reader in = new StringReader(data);
		JavaScriptCompressor compressor = new JavaScriptCompressor(in,
				new ErrorReporter() {

					public void warning(String message, String sourceName,
							int line, String lineSource, int lineOffset) {
						if (line < 0) {
							log.error("\n[WARNING] " + message);
						} else {
							log.error("\n[WARNING] " + line + ':' + lineOffset
									+ ':' + message);
						}
					}

					public void error(String message, String sourceName,
							int line, String lineSource, int lineOffset) {
						if (line < 0) {
							log.error("\n[ERROR] " + message);
						} else {
							log.error("\n[ERROR] " + line + ':' + lineOffset
									+ ':' + message);
						}
					}

					public EvaluatorException runtimeError(String message,
							String sourceName, int line, String lineSource,
							int lineOffset) {
						error(message, sourceName, line, lineSource, lineOffset);
						return new EvaluatorException(message);
					}
				});
		in.close();
		in = null;

		Writer out = new StringWriter();

		boolean munge = true;
		boolean preserveAllSemiColons = true;
		boolean disableOptimizations = false;
		boolean verbose = false;

		compressor.compress(out, 20000, munge, verbose, preserveAllSemiColons,
				disableOptimizations);
		return out.toString();
	}

	public File getFilePath() {
		return filePath;
	}

	public void setFilePath(File filePath) {
		this.filePath = filePath;
	}

	public boolean isMinify() {
		return minify;
	}

	public void setMinify(boolean minify) {
		this.minify = minify;
	}

	public MergeMode getMode() {
		return mode;
	}

	public void setMode(MergeMode mode) {
		this.mode = mode;
	}
}
