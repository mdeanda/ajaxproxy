package com.thedeanda.ajaxproxy.ui.viewer;

import java.net.URL;
import java.util.UUID;

import javax.swing.JPanel;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.RequestListener;

/**
 * this panel is the new implementation of the resource viewer tab. it will be
 * more event driven to allow it to monitor multiple requests as they are
 * happening instead of waiting for them to complete, possibly displaying them
 * out of order.
 * 
 * @author mdeanda
 *
 */
public class RequestViewer extends JPanel implements RequestListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(RequestViewer.class);

	public RequestViewer() {
		super();
	}

	@Override
	public void newRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {
		log.info("new request: {} {} {}", id, url, requestHeaders);

	}

	@Override
	public void requestComplete(UUID id, int status, Header[] responseHeaders,
			byte[] data) {
		log.info("request complete: {} {} {}", id, status, responseHeaders);

	}
}
