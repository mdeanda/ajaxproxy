package com.thedeanda.ajaxproxy;

public interface AccessTracker {
	public void trackFile(String url, int duration);
}
