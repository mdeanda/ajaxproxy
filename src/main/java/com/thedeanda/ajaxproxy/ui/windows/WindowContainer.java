package com.thedeanda.ajaxproxy.ui.windows;

import javax.swing.*;
import java.lang.ref.WeakReference;

public class WindowContainer {
	private WeakReference<JFrame> frame;
	private String id;

	public WindowContainer(String id, JFrame frame) {
		this.id = id;
		this.frame = new WeakReference<>(frame);
	}

	public String getName() {
		String ret = "null";

		JFrame f = getFrame();
		if (f != null) {
			ret = f.getTitle();
		}
		return ret;
	}

	public JFrame getFrame() {
		return frame.get();
	}

	public String getId() {
		return id;
	}
}
