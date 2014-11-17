package com.thedeanda.ajaxproxy.ui.rest;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.thedeanda.ajaxproxy.LoadedResource;
import com.thedeanda.ajaxproxy.ui.ResourcePanel;

public class RestClientFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private RestClientPanel panel;

	public RestClientFrame(LoadedResource resource) {
		panel = new RestClientPanel();
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, panel);
		// panel.setResource(resource);
		setTitle("Ajax Proxy - Rest Client");
		setPreferredSize(new Dimension(1000, 700));
		setMinimumSize(new Dimension(600, 380));
		pack();
	}

	public static void main(String[] args) {
		RestClientFrame f = new RestClientFrame(null);
		f.pack();
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
