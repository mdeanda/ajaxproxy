package com.thedeanda.ajaxproxy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ThrottleFilter implements Filter {

	private long forcedLatency = 0;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (forcedLatency > 0) {
			try {
				Thread.sleep(forcedLatency);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

	public long getForcedLatency() {
		return forcedLatency;
	}

	public void setForcedLatency(long forcedLatency) {
		this.forcedLatency = forcedLatency;
	}

}
