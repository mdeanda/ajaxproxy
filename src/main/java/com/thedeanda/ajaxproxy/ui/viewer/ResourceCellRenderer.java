package com.thedeanda.ajaxproxy.ui.viewer;

import com.thedeanda.ajaxproxy.ui.model.Resource;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ResourceCellRenderer extends JPanel implements
		ListCellRenderer<Resource> {
	private static final long serialVersionUID = -3020786707630237791L;
	private JLabel path;
	private JLabel status;
	private JLabel method;
	private JLabel dur;

	private final Color lightColor = new Color(250, 250, 255);
	private final Color selectedColor = new Color(162, 202, 255);

	private final Color[] slowColors = new Color[] { Color.decode("#fffdf0"),
			Color.decode("#ffd187"), Color.decode("#ffa67f"),
			Color.decode("#ff7b47") };

	private final long[] durForSlow = new long[] { 500, 1000, 2500, 5000 };

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
		layout.putConstraint(SpringLayout.WEST, path, 4, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, path, -4, SpringLayout.EAST,
				this);

		layout.putConstraint(SpringLayout.NORTH, method, 2, SpringLayout.SOUTH,
				path);
		layout.putConstraint(SpringLayout.WEST, method, 4, SpringLayout.WEST,
				this);

		layout.putConstraint(SpringLayout.NORTH, status, 2, SpringLayout.SOUTH,
				path);
		layout.putConstraint(SpringLayout.WEST, status, 4, SpringLayout.EAST,
				method);

		layout.putConstraint(SpringLayout.NORTH, dur, 2, SpringLayout.SOUTH,
				path);
		layout.putConstraint(SpringLayout.EAST, dur, -4, SpringLayout.EAST,
				this);

		this.setPreferredSize(new Dimension(50, 40));
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends Resource> list, Resource resource, int index,
			boolean isSelected, boolean cellHasFocus) {

		Color color = list.getBackground();
		boolean slowColor = false;
		if (index % 2 == 1) {
			color = lightColor;
		}
		// slowColors
		if (resource == null) {
			return this;
		}

		long requestDuration = 0;
		String url = resource.getUrl();
		requestDuration = resource.getDuration();
		if (resource.getUrlObject() != null) {
			URL uo = resource.getUrlObject();
			url = uo.getPath();
			if (uo.getQuery() != null) {
				url += "?" + uo.getQuery();
			}
		}
		path.setText(url);

		String statusText = "";
		if (resource.getStatus() > 0) {
			statusText = String.valueOf(resource.getStatus());
		}
		status.setText(statusText);
		method.setText(resource.getMethod());

		String durText = "";
		if (resource.getDuration() > 0 || resource.getStatus() > 0) {
			// cached requests have duration of 0
			durText = String.valueOf(resource.getDuration()) + "ms";
		}
		dur.setText(durText);

		for (int i = 0; i < durForSlow.length; i++) {
			if (requestDuration > durForSlow[i]) {
				color = slowColors[i];
				slowColor = true;
			}
		}

		if (isSelected) {
			if (slowColor) {
				color = blend(color, selectedColor);
			} else {
				color = selectedColor;
			}
		}

		setForeground(list.getForeground());
		setBackground(color);

		return this;
	}

	private Color blend(Color c0, Color c1) {
		double totalAlpha = c0.getAlpha() + c1.getAlpha();
		double weight0 = c0.getAlpha() / totalAlpha;
		double weight1 = c1.getAlpha() / totalAlpha;

		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
		double a = Math.max(c0.getAlpha(), c1.getAlpha());

		return new Color((int) r, (int) g, (int) b, (int) a);
	}
}
