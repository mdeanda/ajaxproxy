package com.thedeanda.ajaxproxy.http;

import com.thedeanda.ajaxproxy.filter.handler.RequestHandler;
import com.thedeanda.ajaxproxy.model.ProxyContainer;
import org.apache.http.Header;

import java.net.URL;
import java.util.UUID;

public class RequestListenerHelper implements RequestListener {
    private final RequestListener listener;
    private final RequestHandler requestHandler;

    public RequestListenerHelper(RequestListener listener, ProxyContainer proxyContainer) {
        this.listener = listener;
        this.requestHandler = proxyContainer.getRequestHandler();
    }

    @Override
    public void newRequest(UUID id, String url, String method, RequestHandler requestHandler) {
        listener.newRequest(id, url, method, this.requestHandler);
    }

    @Override
    public void startRequest(UUID id, URL url, Header[] requestHeaders, byte[] data) {
        listener.startRequest(id, url, requestHeaders, data);
    }

    @Override
    public void requestComplete(UUID id, int status, String reason, long duration, Header[] responseHeaders, byte[] data) {
        listener.requestComplete(id, status, reason, duration, responseHeaders, data);
    }

    @Override
    public void error(UUID id, String message, Exception e) {
        listener.error(id, message, e);
    }
}
