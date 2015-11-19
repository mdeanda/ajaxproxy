package com.thedeanda.ajaxproxy.ui.json;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.rest.RestClientFrame;
import com.thedeanda.ajaxproxy.ui.windows.WindowContainer;
import com.thedeanda.ajaxproxy.ui.windows.WindowListListener;
import com.thedeanda.ajaxproxy.ui.windows.WindowListListenerCleanup;
import com.thedeanda.ajaxproxy.ui.windows.Windows;

public class JsonViewerFrame extends JFrame implements WindowListListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(JsonViewerFrame.class);
	private JsonViewer panel;
	final JFileChooser fc = new JFileChooser();
	private File file;
	private String windowId;

	public JsonViewerFrame() {
		panel = new JsonViewer();
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, panel);
		panel.setDefaultButton();
		// panel.setResource(resource);
		setTitle("Json Viewer - Ajax Proxy");
		setPreferredSize(new Dimension(900, 700));
		setMinimumSize(new Dimension(500, 380));
		initMenuBar();

		URL imgUrl = ClassLoader.getSystemClassLoader().getResource("icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(imgUrl);
		this.setIconImage(image);

		pack();
		this.windowId = Windows.get().addListener(this).add(this);
		this.addWindowListener(new WindowListListenerCleanup(this));
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

		mi = new JMenuItem("Open");
		mi.setMnemonic(KeyEvent.VK_O);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleOpen();
			}
		});
		menu.add(mi);

		menu.addSeparator();

		mi = new JMenuItem("Rest Client");
		mi.setMnemonic(KeyEvent.VK_R);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleRest();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Json Viewer");
		mi.setMnemonic(KeyEvent.VK_J);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleJson();
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
		panel.setText("");
		panel.format();
	}

	private void handleOpen() {
		if (file != null)
			fc.setCurrentDirectory(file);
		int retVal = fc.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			String fileContents = "";
			try {
				fileContents = FileUtils.readFileToString(file);
				panel.setText(fileContents);
				panel.format();
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		}
	}

	private void handleJson() {
		JsonViewerFrame frame = new JsonViewerFrame();
		frame.setVisible(true);
	}

	private void handleExit() {
		this.dispose();
	}

	private void handleRest() {
		RestClientFrame frame = new RestClientFrame();
		frame.setVisible(true);
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
