package com.thedeanda.ajaxproxy.ui.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.UIDefaults;
import javax.swing.border.Border;

public class BottomBorder implements Border {

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y0, int width,
			int height) {
		Graphics2D g2 = (Graphics2D) g;
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		final Color light = defaults.getColor("SplitPane.highlight");
		final Color dark = defaults.getColor("SplitPane.darkShadow");

		for (int i = 0; i < width; i += 4) {
			int y = height - 2;
			g.setColor(light);
			g.fillRect(i, y, 2, 2);
			g.setColor(dark);
			g.fillRect(i, y, 1, 1);
		}
		//g.drawLine(0, height - 1, width, height - 1);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 2, 0);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

}
