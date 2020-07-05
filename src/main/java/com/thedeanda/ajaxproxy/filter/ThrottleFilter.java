package com.thedeanda.ajaxproxy.filter;

import javax.servlet.*;
import java.io.IOException;

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
