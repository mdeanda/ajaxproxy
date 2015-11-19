package com.thedeanda.ajaxproxy.ui.windows;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Windows {
	private static Windows instance = null;
	private static Logger log = LoggerFactory.getLogger(Windows.class);
	private static AtomicInteger nextId = new AtomicInteger();

	private Map<String, WindowContainer> frames = new TreeMap<>();
	private Set<WindowListListener> listeners = new HashSet<>();

	private Windows() {

	}

	public static Windows get() {
		if (instance == null) {
			instance = new Windows();
		}
		return instance;
	}

	public Windows addListener(WindowListListener listener) {
		listeners.add(listener);
		notifyOfChange(getCurrentWindows(), listener);
		return this;
	}

	public String add(final JFrame window) {
		final String id = String.valueOf(nextId.addAndGet(1));
		frames.put(id, new WindowContainer(id, window));

		notifyOfChange();

		window.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				Windows.this.remove(id);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

		});

		return id;
	}

	protected void remove(String id) {
		frames.remove(id);
		notifyOfChange();
	}

	public void removeListener(WindowListListener listener) {
		listeners.remove(listener);
	}

	private void notifyOfChange() {
		final Collection<WindowContainer> windows = getCurrentWindows();
		log.debug("notify of change: {}", windows.size());

		for (final WindowListListener listener : listeners) {
			notifyOfChange(windows, listener);
		}
	}

	private void notifyOfChange(final Collection<WindowContainer> windows,
			final WindowListListener listener) {
		log.debug("notify of change: {}", windows.size());

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					listener.windowsChanged(windows);
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}
		});
	}

	public Collection<WindowContainer> getCurrentWindows() {
		List<WindowContainer> ret = new ArrayList<>();
		List<WindowContainer> itemsToRemove = new ArrayList<>();

		for (WindowContainer wc : frames.values()) {
			JFrame frame = wc.getFrame();
			if (frame == null) {
				itemsToRemove.add(wc);
			} else {
				ret.add(wc);
			}
		}

		for (WindowContainer wc : itemsToRemove) {
			frames.remove(wc.getId());
		}

		return ret;
	}
}
