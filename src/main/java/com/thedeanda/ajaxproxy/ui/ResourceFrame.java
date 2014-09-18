package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.thedeanda.ajaxproxy.LoadedResource;

/**
 * simple frame to view resources in a separate window
 * 
 * @author mdeanda
 *
 */
public class ResourceFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private ResourcePanel panel;

	public ResourceFrame(LoadedResource resource) {
		panel = new ResourcePanel(true);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, panel);
		panel.setResource(resource);
		setTitle(resource.getUrl());
		setPreferredSize(new Dimension(640, 480));
		pack();
	}
}
