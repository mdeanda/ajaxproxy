package com.thedeanda.ajaxproxy.ui.json;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Collection;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.windows.WindowContainer;
import com.thedeanda.ajaxproxy.ui.windows.WindowListListener;
import com.thedeanda.ajaxproxy.ui.windows.WindowListListenerCleanup;
import com.thedeanda.ajaxproxy.ui.windows.Windows;

public class JsonViewerFrame extends JFrame implements WindowListListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(JsonViewerFrame.class);
	private JsonViewer panel;
	private String windowId;

	public JsonViewerFrame() {
		panel = new JsonViewer();
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, panel);
		panel.setDefaultButton();
		// panel.setResource(resource);
		setTitle("Ajax Proxy - Json Viewer");
		setPreferredSize(new Dimension(900, 700));
		setMinimumSize(new Dimension(500, 380));

		URL imgUrl = ClassLoader.getSystemClassLoader().getResource("icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(imgUrl);
		this.setIconImage(image);

		pack();
		this.windowId = Windows.get().addListener(this).add(this);
		this.addWindowListener(new WindowListListenerCleanup(this));
	}

	public static void main(String[] args) {
		JsonViewerFrame f = new JsonViewerFrame();
		f.pack();
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	@Override
	public void windowsChanged(Collection<WindowContainer> windows) {
		// TODO Auto-generated method stub
		
	}

}
