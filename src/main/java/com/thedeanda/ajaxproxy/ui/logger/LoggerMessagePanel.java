package com.thedeanda.ajaxproxy.ui.logger;

import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessage;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ContentViewer;

import javax.swing.*;

public class LoggerMessagePanel extends JPanel {
	private ContentViewer cv;

	public LoggerMessagePanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		cv = new ContentViewer();
		add(cv);

		layout.putConstraint(SpringLayout.NORTH, cv, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, cv, -10, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, cv, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, cv, -10, SpringLayout.EAST, this);
	}

	public void setLoggerMessage(LoggerMessage message) {
		if (message == null) {
			cv.setContent((byte[]) null);
		} else {
			cv.setContent(message.getMessage());
		}
	}

}
