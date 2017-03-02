package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.model.config.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.ui.SwingUtils;

public class ProxyEditorPanel extends JPanel {
	private static final long serialVersionUID = 5379224168584631339L;
	private SpringLayout layout;
	private JLabel domainLabel;
	private JTextField hostField;
	private JLabel portLabel;
	private JTextField portField;
	private JLabel pathLabel;
	private JTextField pathField;
	private JLabel hostHeaderLabel;
	private JTextField hostHeaderField;
	private JButton btn;
	private JCheckBox cacheCheckbox;

	private ProxyConfigRequest result = null;

	public ProxyEditorPanel() {
		layout = new SpringLayout();
		setLayout(layout);

		domainLabel = new JLabel("Host");
		hostField = SwingUtils.newJTextField();
		addListeners(hostField);
		add(domainLabel);
		add(hostField);

		portLabel = new JLabel("Port");
		portField = SwingUtils.newJTextField();
		addListeners(portField);
		add(portLabel);
		add(portField);

		pathLabel = new JLabel("Path");
		pathField = SwingUtils.newJTextField();
		addListeners(pathField);
		add(pathLabel);
		add(pathField);

		hostHeaderLabel = new JLabel("Host Header");
		hostHeaderField = SwingUtils.newJTextField();
		addListeners(hostHeaderField);
		add(hostHeaderLabel);
		add(hostHeaderField);

		btn = new JButton("Ok");
		btn.setMargin(new Insets(2, 14, 2, 14));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commitEdit();
			}
		});
		add(btn);

		cacheCheckbox = new JCheckBox("Cache Requests");
		cacheCheckbox.setToolTipText("Any non-GET request clears the cache for this proxy mapping");
		add(cacheCheckbox);

		initLayout();
		setPreferredSize(new Dimension(700, 120));
		setMinimumSize(new Dimension(500, 120));
	}

	private void addListeners(final JTextField txtField) {
		txtField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					commitEdit();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initLayout() {
		JLabel[] labels = new JLabel[] { domainLabel, portLabel, pathLabel };
		Component[] fields = new Component[] { hostField, portField, pathField };
		int[] cols = new int[] { 275, 70, 250 };

		for (int i = cols.length - 1; i >= 0; i--) {
			JLabel lbl = labels[i];
			Component fld = fields[i];
			if (i == 0) {
				layout.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, this);

				layout.putConstraint(SpringLayout.NORTH, fld, 2, SpringLayout.SOUTH, lbl);

				layout.putConstraint(SpringLayout.WEST, fld, 0, SpringLayout.WEST, this);

				layout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, this);
			} else {
				layout.putConstraint(SpringLayout.BASELINE, fld, 0, SpringLayout.BASELINE, fields[0]);

				layout.putConstraint(SpringLayout.WEST, fld, 10, SpringLayout.EAST, fields[i - 1]);
				if (lbl != null) {
					layout.putConstraint(SpringLayout.VERTICAL_CENTER, lbl, 0, SpringLayout.VERTICAL_CENTER, labels[0]);
					layout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, fld);
				}
			}
			if (i == cols.length - 1) {
				layout.putConstraint(SpringLayout.EAST, fld, -10, SpringLayout.WEST, btn);
			} else {
				layout.putConstraint(SpringLayout.EAST, fld, cols[i], SpringLayout.WEST, fld);
			}

		}
		layout.putConstraint(SpringLayout.BASELINE, btn, 0, SpringLayout.BASELINE, hostField);
		layout.putConstraint(SpringLayout.EAST, btn, 0, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, hostHeaderLabel, 5, SpringLayout.SOUTH, fields[0]);
		layout.putConstraint(SpringLayout.WEST, hostHeaderLabel, 0, SpringLayout.WEST, labels[0]);

		layout.putConstraint(SpringLayout.NORTH, hostHeaderField, 5, SpringLayout.SOUTH, hostHeaderLabel);
		layout.putConstraint(SpringLayout.WEST, hostHeaderField, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, hostHeaderField, 0, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, cacheCheckbox, 5, SpringLayout.SOUTH, hostHeaderField);
		layout.putConstraint(SpringLayout.WEST, cacheCheckbox, 0, SpringLayout.WEST, hostHeaderLabel);

	}

	public void startEdit(ProxyConfigRequest config) {
		if (config == null) {
			config = new ProxyConfigRequest();
		}
		String sport = "";
		if (config.getPort() > 0)
			sport = String.valueOf(config.getPort());

		this.hostField.setText(config.getHost());
		this.portField.setText(sport);
		this.pathField.setText(config.getPath());

		this.cacheCheckbox.setSelected(config.isEnableCache());
	}

	private void commitEdit() {
		String host = hostField.getText();
		int port = 0;
		try {
			String sport = portField.getText();
			if (!StringUtils.isBlank(sport)) {
				port = Integer.parseInt(sport);
			}
		} catch (NumberFormatException nfe) {
			port = 0;
		}
		String path = pathField.getText();

		ProxyConfigRequest config = new ProxyConfigRequest();
		config.setHost(host);
		config.setPort(port);
		config.setPath(path);
		config.setEnableCache(cacheCheckbox.isSelected());

		result = config;
	}

	public ProxyConfigRequest getResult() {
		return result;
	}
}
