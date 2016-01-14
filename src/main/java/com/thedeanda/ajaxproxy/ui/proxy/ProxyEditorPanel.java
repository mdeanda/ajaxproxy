package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.model.config.ProxyConfig;
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
	private JButton btn;
	private EditorListener listener;
	private JCheckBox cacheCheckbox;
	private JCheckBox newProxyCheckbox;

	public ProxyEditorPanel(EditorListener listener) {
		this.listener = listener;
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

		btn = new JButton("Ok");
		btn.setMargin(new Insets(2, 14, 2, 14));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commitEdit();
			}
		});
		add(btn);

		newProxyCheckbox = new JCheckBox("Use New Proxy");
		newProxyCheckbox
				.setToolTipText("New proxy implementation does not rely on the old Jetty transparent proxy servlet. Paths should be regular expressions instead of web.xml style paths. (Usually it means to change a /* to /.*)");
		add(newProxyCheckbox);

		cacheCheckbox = new JCheckBox("Cache Requests");
		cacheCheckbox
				.setToolTipText("Any non-GET request clears the cache for this proxy mapping");
		add(cacheCheckbox);

		initNewProxyListener();
		initLayout();
		setPreferredSize(new Dimension(700, 80));
		setMinimumSize(new Dimension(500, 80));
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
		txtField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				txtField.select(0, txtField.getText().length());
			}

			@Override
			public void focusLost(FocusEvent e) {
				txtField.select(0, 0);
			}
		});
	}

	private void initNewProxyListener() {
		cacheCheckbox.setEnabled(newProxyCheckbox.isSelected());
		newProxyCheckbox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				cacheCheckbox.setEnabled(newProxyCheckbox.isSelected());
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
				layout.putConstraint(SpringLayout.NORTH, lbl, 0,
						SpringLayout.NORTH, this);

				layout.putConstraint(SpringLayout.NORTH, fld, 2,
						SpringLayout.SOUTH, lbl);

				layout.putConstraint(SpringLayout.WEST, fld, 0,
						SpringLayout.WEST, this);

				layout.putConstraint(SpringLayout.WEST, lbl, 0,
						SpringLayout.WEST, this);
			} else {
				layout.putConstraint(SpringLayout.BASELINE, fld, 0,
						SpringLayout.BASELINE, fields[0]);

				layout.putConstraint(SpringLayout.WEST, fld, 10,
						SpringLayout.EAST, fields[i - 1]);
				if (lbl != null) {
					layout.putConstraint(SpringLayout.VERTICAL_CENTER, lbl, 0,
							SpringLayout.VERTICAL_CENTER, labels[0]);
					layout.putConstraint(SpringLayout.WEST, lbl, 0,
							SpringLayout.WEST, fld);
				}
			}
			if (i == cols.length - 1) {
				layout.putConstraint(SpringLayout.EAST, fld, -10,
						SpringLayout.WEST, btn);
			} else {
				layout.putConstraint(SpringLayout.EAST, fld, cols[i],
						SpringLayout.WEST, fld);
			}

		}
		layout.putConstraint(SpringLayout.BASELINE, btn, 0,
				SpringLayout.BASELINE, hostField);
		layout.putConstraint(SpringLayout.EAST, btn, 0, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, newProxyCheckbox, 5,
				SpringLayout.SOUTH, fields[0]);
		layout.putConstraint(SpringLayout.WEST, newProxyCheckbox, 0,
				SpringLayout.WEST, labels[0]);

		layout.putConstraint(SpringLayout.NORTH, cacheCheckbox, 0,
				SpringLayout.NORTH, newProxyCheckbox);
		layout.putConstraint(SpringLayout.WEST, cacheCheckbox, 15,
				SpringLayout.EAST, newProxyCheckbox);

	}

	public void startEdit(ProxyConfig config) {
		if (config == null) {
			config = new ProxyConfig();
		}
		String sport = "";
		if (config.getPort() > 0)
			sport = String.valueOf(config.getPort());

		this.hostField.setText(config.getHost());
		this.portField.setText(sport);
		this.pathField.setText(config.getPath());

		this.cacheCheckbox.setSelected(config.isEnableCache());
		newProxyCheckbox.setSelected(config.isNewProxy());
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

		ProxyConfig config = new ProxyConfig();
		config.setHost(host);
		config.setPort(port);
		config.setPath(path);
		config.setNewProxy(newProxyCheckbox.isSelected());
		config.setEnableCache(cacheCheckbox.isSelected());
		listener.commitChanges(config);
	}
}
