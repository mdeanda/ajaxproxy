package com.thedeanda.ajaxproxy.ui.rest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.busy.BusyNotification;
import com.thedeanda.ajaxproxy.ui.model.Resource;
import com.thedeanda.ajaxproxy.ui.windows.WindowContainer;
import com.thedeanda.ajaxproxy.ui.windows.WindowListListener;
import com.thedeanda.ajaxproxy.ui.windows.WindowListListenerCleanup;
import com.thedeanda.ajaxproxy.ui.windows.WindowMenuHelper;
import com.thedeanda.ajaxproxy.ui.windows.Windows;

public class RestClientFrame extends JFrame implements RequestListener,
		WindowListListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(RestClientFrame.class);
	private RestClientPanel panel;
	private BusyNotification busy;
	private String windowId;

	private static final Set<String> BLACKLIST_HEADERS = new HashSet<String>();
	static {
		BLACKLIST_HEADERS.add("Host");
		BLACKLIST_HEADERS.add("Content-Length");
	}

	public RestClientFrame() {
		panel = new RestClientPanel();
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, panel);
		panel.setDefaultButton();
		panel.setListener(this);
		// panel.setResource(resource);
		setTitle("Rest Client - Ajax Proxy");
		setPreferredSize(new Dimension(1000, 700));
		setMinimumSize(new Dimension(600, 380));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		busy = new BusyNotification();
		setGlassPane(busy);

		URL imgUrl = ClassLoader.getSystemClassLoader().getResource("icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(imgUrl);
		this.setIconImage(image);

		initMenuBar();
		pack();
		this.windowId = Windows.get().addListener(this).add(this);
		this.addWindowListener(new WindowListListenerCleanup(this));
		new WindowMenuHelper(windowId, getJMenuBar());
	}

	public void fromResource(Resource resource) {
		panel.setUrl(resource.getUrl());
		// TODO: consider keeping byte data as is depending on content type
		byte[] inputData = resource.getInputData();
		String input = "";
		if (inputData != null) {
			input = new String(inputData);
		}

		panel.setInput(input);
		panel.setMethod(resource.getMethod());

		addHeaders(resource.getRequestHeaders());
	}

	private void addHeaders(Header[] headers) {
		panel.setHeaders("");
		if (headers != null) {
			StringBuilder sb = new StringBuilder();
			for (Header h : headers) {
				if (!BLACKLIST_HEADERS.contains(h.getName())) {
					sb.append(h.getName() + ": " + h.getValue() + "\n");
				}
			}
			panel.setHeaders(sb.toString());
		}
	}

	private void initMenuBar() {
		JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);
		JMenu menu;
		JMenuItem mi;

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		mb.add(menu);

		mi = new JMenuItem("New");
		mi.setMnemonic(KeyEvent.VK_N);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleNew();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Save");
		mi.setMnemonic(KeyEvent.VK_S);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleSave();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Clone");
		mi.setMnemonic(KeyEvent.VK_L);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleClone();
			}
		});
		menu.add(mi);

		menu.addSeparator();

		mi = new JMenuItem("Exit");
		mi.setMnemonic(KeyEvent.VK_X);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleExit();
			}
		});
		menu.add(mi);
	}

	private void handleNew() {
		RestClientFrame rest = new RestClientFrame();
		rest.setVisible(true);
	}

	private void handleSave() {
		panel.saveCurrent();
	}

	private void handleClone() {
		RestClientFrame rest = new RestClientFrame();
		rest.panel.setUrl(panel.getUrl());
		rest.panel.setHeaders(panel.getHeaders());
		rest.panel.setInput(panel.getInput());
		rest.panel.setMethod(panel.getMethod());
		rest.setVisible(true);
	}

	private void handleExit() {
		this.dispose();
	}

	private void busy() {
		busy.setVisible(true);
	}

	private void notBusy() {
		busy.setVisible(false);
	}

	@Override
	public void newRequest(UUID id, String url, String method) {
		busy();
	}

	@Override
	public void startRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {
	}

	@Override
	public void requestComplete(UUID id, int status, String reason,
			long duation, Header[] responseHeaders, byte[] data) {
		log.debug("request complete");
		notBusy();
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		log.debug("request complete (error)", e);
		notBusy();
	}

	@Override
	public void windowsChanged(Collection<WindowContainer> windows) {
		for (WindowContainer wc : windows) {
			log.info(wc.getName());
		}

	}
}
