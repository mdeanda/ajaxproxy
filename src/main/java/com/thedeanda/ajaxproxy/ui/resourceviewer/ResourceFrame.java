package com.thedeanda.ajaxproxy.ui.resourceviewer;

import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.ui.model.Resource;

import javax.swing.*;
import java.awt.*;

/**
 * simple frame to view resources in a separate window
 * 
 * @author mdeanda
 *
 */
public class ResourceFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private ResourcePanel panel;

	public ResourceFrame(ResourceService resourceService, Resource resource) {
		panel = new ResourcePanel(resourceService, true);
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, panel);
		panel.setResource(resource);
		setTitle(resource.getPath());
		setPreferredSize(new Dimension(640, 480));
		pack();
	}
}
