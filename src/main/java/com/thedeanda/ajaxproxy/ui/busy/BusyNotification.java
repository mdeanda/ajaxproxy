package com.thedeanda.ajaxproxy.ui.busy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BusyNotification extends JComponent {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private int offset = 0;
	private static final int SPACE = 12;
	private static final int WIDTH = 4;
	private static final int HEIGHT = 3;

	public BusyNotification() {
		setPreferredSize(new Dimension(100, 100));

		timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				offset++;
				if (offset >= SPACE) {
					offset = 0;
				}
				repaint();
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			timer.start();
		} else {
			timer.stop();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.DARK_GRAY);
		Dimension size = getSize();
		for (int i = offset; i < size.width + SPACE; i += SPACE) {
			g.fillRect(i, 0, WIDTH, HEIGHT);
		}
	}
}
