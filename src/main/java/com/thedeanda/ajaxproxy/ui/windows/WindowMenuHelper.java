package com.thedeanda.ajaxproxy.ui.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowMenuHelper implements WindowListListener {
	private static final Logger log = LoggerFactory
			.getLogger(WindowMenuHelper.class);
	private String windowId;
	private JMenuBar menuBar;
	private JMenu menu;

	public WindowMenuHelper(String windowId, JMenuBar menuBar) {
		this.windowId = windowId;
		this.menuBar = menuBar;

		menu = new JMenu("Window");
		menu.setMnemonic(KeyEvent.VK_W);
		menuBar.add(menu);

		reset();
		Windows.get().addListener(this);
	}

	private void reset() {
		JMenuItem mi;

	}

	@Override
	public void windowsChanged(Collection<WindowContainer> windows) {
		log.info("windows changed {}", windowId);
		// first check if our window got closed...
		boolean stillOpen = false;
		for (WindowContainer wc : windows) {
			if (windowId.equals(wc.getId())) {
				stillOpen = true;
				break;
			}
		}
		if (!stillOpen) {
			// TODO: stop listening
			log.info("stop listening from: {}", windowId);
			Windows.get().removeListener(this);
			return;
		}

		JMenuItem mi;
		menu.removeAll();
		for (final WindowContainer wc : windows) {
			mi = new JMenuItem(wc.getName());
			menu.add(mi);
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFrame frame = wc.getFrame();
					if (frame != null) {
						frame.setVisible(true);
						frame.requestFocus();
					}
				}
			});
		}

	}
}
