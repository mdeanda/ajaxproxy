package com.thedeanda.ajaxproxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class APFilter implements Filter {
	private static final Logger log = Logger.getLogger(APFilter.class);
	private int maxBitrate = 0; // in KBps
	private int forcedLatency = 50;
	private boolean logRequests = false;
	private String appendToPath = "";
	private Semaphore throttleLock;
	private Pattern logExpression;
	private Pattern contentExpression;
	private List<AccessTracker> trackers = new ArrayList<AccessTracker>();
	private Thread trackerThread;
	private List<TrackItem> trackBuffer = new LinkedList<TrackItem>();
	private boolean logInput;
	private boolean logOutput;
	private boolean logCookies;

	@Override
	public void destroy() {
		if (trackerThread != null) {
			trackerThread.interrupt();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			MyServletRequestWrapper reqWrapper = new MyServletRequestWrapper(
					httpRequest);
			MyServletResponseWrapper wrapper = new MyServletResponseWrapper(
					httpResponse);
			StringBuffer sb = new StringBuffer();
			boolean stillLogRequests = logRequests;
			String url = httpRequest.getRequestURI();

			// TODO: track duration
			trackAccess(url, 0);

			if (stillLogRequests) {
				if (logExpression != null) {
					Matcher matcher = logExpression.matcher(url);
					stillLogRequests = matcher.matches();
				}
			}
			if (stillLogRequests) {
				String method = httpRequest.getMethod();
				sb.append(method);
				sb.append(" ");
				sb.append(url);
				String qs = httpRequest.getQueryString();
				if (null != qs && !"".equals(qs))
					sb.append("?" + qs);
				sb.append("\n");
				if (("POST".equals(method) || "PUT".equals(method)) && logInput) {
					sb.append("Input:\n");
					StringBuffer inputBuffer = new StringBuffer();
					@SuppressWarnings("unchecked")
					List<String> lines = IOUtils.readLines(reqWrapper
							.getClonedInputStream());
					for (String line : lines) {
						inputBuffer.append(line);
						inputBuffer.append("\n");
					}
					sb.append(inputBuffer.toString().trim());
					sb.append("\n");
				}
			}

			if (forcedLatency > 0) {
				try {
					Thread.sleep(this.forcedLatency);
				} catch (InterruptedException e) {
					// we probably don't need to continue processing
					return;
				}
			}
			chain.doFilter(reqWrapper, wrapper);
			throttledCopy(wrapper.getNewInputStream(),
					response.getOutputStream());

			if (logCookies && stillLogRequests) {
				// TODO: add "log cookies" flag
				Cookie[] cookies = ((HttpServletRequest) request).getCookies();
				if (cookies != null && cookies.length > 0) {
					sb.append("Cookies:\n");
					for (Cookie c : cookies) {
						sb.append("    name: " + c.getName() + ", domain: "
								+ c.getDomain() + ", path: " + c.getPath()
								+ ", value: " + c.getValue() + "\n");
					}
				} else {
					sb.append("Cookies: none\n");
				}
			}

			if (stillLogRequests) {
				StringBuffer outputBuffer = null;
				if (logOutput || contentExpression != null) {
					@SuppressWarnings("unchecked")
					List<String> lines = IOUtils.readLines(wrapper
							.getNewInputStream());
					outputBuffer = new StringBuffer();
					for (String line : lines) {
						outputBuffer.append(line);
						outputBuffer.append("\n");
					}
				}
				if (contentExpression != null) {
					Matcher matcher = contentExpression.matcher(outputBuffer
							.toString());
					stillLogRequests = matcher.matches();
				}
				if (logOutput) {
					sb.append("Output:\n");
					sb.append(outputBuffer.toString().trim());
					sb.append("\n"); // extra line to make it easier to read
				}
			}

			if (stillLogRequests) {
				log.info(sb.toString());
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		throttleLock = new Semaphore(1);
		trackerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!Thread.interrupted()) {
						List<TrackItem> localBuffer = new ArrayList<TrackItem>();
						synchronized (trackBuffer) {
							if (trackBuffer.isEmpty())
								trackBuffer.wait();
							while (!trackBuffer.isEmpty())
								localBuffer.add(trackBuffer.remove(0));
						}

						for (TrackItem ti : localBuffer) {
							for (AccessTracker at : trackers) {
								at.trackFile(ti.file, ti.duration);
							}
						}
					}
				} catch (InterruptedException e) {

				}
			}
		});
		trackerThread.start();
	}

	public int getMaxBitrate() {
		return maxBitrate;
	}

	public void setMaxBitrate(int maxBitrate) {
		this.maxBitrate = maxBitrate;
	}

	public int getForcedLatency() {
		return forcedLatency;
	}

	public void setForcedLatency(int forcedLatency) {
		log.trace("setting forced latency: " + forcedLatency);
		this.forcedLatency = forcedLatency;
	}

	private void throttledCopy(ByteArrayInputStream is, OutputStream os) {
		boolean release = false;
		try {
			if (this.maxBitrate > 0) {
				throttleLock.acquire();
				release = true;

				int blocks = 10;
				int bytesPerBlock = maxBitrate * 1024 / blocks;
				byte[] buffer = new byte[bytesPerBlock];
				int delay = 1000 / blocks;
				int read = 0;

				do {
					read = is.read(buffer);
					if (read > 0)
						os.write(buffer, 0, read);
					Thread.sleep(delay);
				} while (read > 0);

				// TODO: actually throttle it
				// IOUtils.copy(is, os);
			} else {
				IOUtils.copy(is, os);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (release)
				throttleLock.release();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class MyServletRequestWrapper extends HttpServletRequestWrapper {

		private byte[] data;

		public MyServletRequestWrapper(HttpServletRequest request) {
			super(request);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				IOUtils.copy(request.getInputStream(), baos);
			} catch (IOException e) {
			} finally {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}

			this.data = baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream() {
			return new MyServletInputStream(getClonedInputStream());
		}

		public ByteArrayInputStream getClonedInputStream() {
			return new ByteArrayInputStream(data);
		}
	}

	private class MyServletInputStream extends ServletInputStream {
		private InputStream is;

		public MyServletInputStream(InputStream is) {
			this.is = is;
		}

		@Override
		public int read() throws IOException {
			return is.read();
		}
	}

	private class MyServletResponseWrapper extends HttpServletResponseWrapper {
		private ByteArrayOutputStream baos;
		private PrintWriter writer;
		private MyServletOutputStream os;

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
			return new ByteArrayInputStream(baos.toByteArray());
		}
	}

	private class MyServletOutputStream extends ServletOutputStream {
		private OutputStream os;

		public MyServletOutputStream(OutputStream os) {
			this.os = os;
		}

		@Override
		public void write(int bite) throws IOException {
			os.write(bite);
		}
	}

	public boolean isLogRequests() {
		return logRequests;
	}

	public String getAppendToPath() {
		return appendToPath;
	}

	public void setLogRequests(boolean logRequests) {
		this.logRequests = logRequests;
	}

	public void setAppendToPath(String appendToPath) {
		this.appendToPath = appendToPath;
	}

	public void setLogExpression(Pattern logExpression) {
		this.logExpression = logExpression;
	}

	public Pattern getContentExpression() {
		return contentExpression;
	}

	public void setContentExpression(Pattern contentExpression) {
		this.contentExpression = contentExpression;
	}

	public void add(AccessTracker tracker) {
		trackers.add(tracker);
	}

	private void trackAccess(String path, int duration) {
		TrackItem ti = new TrackItem();
		ti.file = path;
		ti.duration = duration;
		synchronized (trackBuffer) {
			trackBuffer.add(ti);
			trackBuffer.notify();
		}
	}

	private class TrackItem {
		public String file;
		public int duration;
	}

	public boolean isLogInput() {
		return logInput;
	}

	public void setLogInput(boolean logInput) {
		this.logInput = logInput;
	}

	public boolean isLogOutput() {
		return logOutput;
	}

	public void setLogOutput(boolean logOutput) {
		this.logOutput = logOutput;
	}

	public boolean isLogCookies() {
		return logCookies;
	}

	public void setLogCookies(boolean logCookies) {
		this.logCookies = logCookies;
	}
}
