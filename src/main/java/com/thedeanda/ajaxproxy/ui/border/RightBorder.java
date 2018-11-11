package com.thedeanda.ajaxproxy.ui.border;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class RightBorder implements Border {

	@Override
	public void paintBorder(Component c, Graphics g, int x0, int y, int width,
			int height) {
		// Graphics2D g2 = (Graphics2D) g;
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		final Color light = defaults.getColor("SplitPane.highlight");
		final Color dark = defaults.getColor("SplitPane.darkShadow");

		for (int i = 0; i < height; i += 4) {
			int x = width - 2;
			g.setColor(light);
			g.fillRect(x, i, 2, 2);
			g.setColor(dark);
			g.fillRect(x, i, 1, 1);
		}
		// g.drawLine(0, height - 1, width, height - 1);
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
