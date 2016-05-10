package com.thedeanda.ajaxproxy.ui.windows;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowListListenerCleanup implements WindowListener {

	private WindowListListener listener;

	public WindowListListenerCleanup(WindowListListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		Windows.get().removeListener(listener);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}
