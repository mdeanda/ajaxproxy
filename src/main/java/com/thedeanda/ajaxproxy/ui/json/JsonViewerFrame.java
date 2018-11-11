package com.thedeanda.ajaxproxy.ui.json;

import com.thedeanda.ajaxproxy.ui.windows.WindowContainer;
import com.thedeanda.ajaxproxy.ui.windows.WindowListListener;
import com.thedeanda.ajaxproxy.ui.windows.WindowMenuHelper;
import com.thedeanda.ajaxproxy.ui.windows.Windows;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		URL imgUrl = ClassLoader.getSystemClassLoader().getResource("icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(imgUrl);
		this.setIconImage(image);

		this.windowId = Windows.get().add(this);
		initMenuBar();
		pack();
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

		mi = new JMenuItem("Exit");
		mi.setMnemonic(KeyEvent.VK_X);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleExit();
			}
		});
		menu.add(mi);

		new WindowMenuHelper(windowId, mb);
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

	private void handleExit() {
		this.dispose();
	}

	@Override
	public void windowsChanged(Collection<WindowContainer> windows) {
		// TODO Auto-generated method stub

	}

}
