package com.thedeanda.ajaxproxy.ui.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;

import com.thedeanda.ajaxproxy.LoadedResource;
import com.thedeanda.ajaxproxy.ui.model.Resource;

public class ResourceCellRenderer extends JPanel implements
		ListCellRenderer<Resource> {
	private static final long serialVersionUID = -3020786707630237791L;
	private JLabel path;
	private JLabel status;
	private JLabel method;
	private JLabel dur;

	public ResourceCellRenderer() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder());

		path = new JLabel("path");
		status = new JLabel("status");
		method = new JLabel("method");
		dur = new JLabel("dur");

		add(path);
		add(status);
		add(method);
		add(dur);

		layout.putConstraint(SpringLayout.NORTH, path, 5, SpringLayout.NORTH,
				this);
		layout.putConstraint(SpringLayout.WEST, path, 2, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, path, -2, SpringLayout.EAST,
				this);

		layout.putConstraint(SpringLayout.NORTH, method, 2, SpringLayout.SOUTH,
				path);
		layout.putConstraint(SpringLayout.WEST, method, 2, SpringLayout.WEST,
				this);

		layout.putConstraint(SpringLayout.NORTH, status, 2, SpringLayout.SOUTH,
				path);
		layout.putConstraint(SpringLayout.WEST, status, 2, SpringLayout.EAST,
				method);

		layout.putConstraint(SpringLayout.NORTH, dur, 2, SpringLayout.SOUTH,
				path);
		layout.putConstraint(SpringLayout.EAST, dur, -2, SpringLayout.EAST,
				this);

		this.setPreferredSize(new Dimension(50, 40));
	}

	@Override
	public Component getListCellRendererComponent(JList list,
			Resource resource, int index, boolean isSelected,
			boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			if (index % 2 == 0) {
				setBackground(list.getBackground());
			} else {
				Color lightColor = new Color(245, 240, 245);
				setBackground(lightColor);
			}
			setForeground(list.getForeground());
		}

		LoadedResource lr = resource.getLoadedResource();
		if (lr != null) {
			path.setText(lr.getPath());
			status.setText(String.valueOf(lr.getStatusCode()));
			method.setText(lr.getMethod());
			dur.setText(lr.getDuration() + "ms");
		} else {
			path.setText(resource.getUrl());
			status.setText("xx");
			method.setText(resource.getMethod());
			dur.setText("");
		}

		return this;
	}

}
