package com.thedeanda.ajaxproxy.ui.serverconfig.proxy;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.config.model.StringVariable;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.ui.util.SwingUtils;

public class RequestProxyEditorPanel extends JPanel implements EditorPanel<ProxyConfigRequest> {
	private static final long serialVersionUID = 5379224168584631339L;
	private static final String[] protocolList = { "http", "https" };

	private SpringLayout layout;
	private JLabel domainLabel;
	private JTextField hostField;
	private JLabel portLabel;
	private JTextField portField;
	private JLabel pathLabel;
	private JTextField pathField;
	private JLabel hostHeaderLabel;
	private JTextField hostHeaderField;
	private JCheckBox cacheCheckbox;
	private JComboBox<String> protocols;
	private JLabel protLabel;

	public RequestProxyEditorPanel() {
		layout = new SpringLayout();
		setLayout(layout);

		domainLabel = new JLabel("Host");
		hostField = SwingUtils.newJTextField();
		add(domainLabel);
		add(hostField);

		portLabel = new JLabel("Port");
		portField = SwingUtils.newJTextField();
		add(portLabel);
		add(portField);

		pathLabel = new JLabel("Path");
		pathField = SwingUtils.newJTextField();
		add(pathLabel);
		add(pathField);

		hostHeaderLabel = new JLabel("Host Header");
		hostHeaderField = SwingUtils.newJTextField();
		add(hostHeaderLabel);
		add(hostHeaderField);

		cacheCheckbox = new JCheckBox("Cache Requests");
		cacheCheckbox.setToolTipText("Any non-GET request clears the cache for this proxy mapping");
		add(cacheCheckbox);

		protLabel = new JLabel("Protocol");
		add(protLabel);

		protocols = new JComboBox<String>(protocolList);
		add(protocols);

		initLayout();
		setPreferredSize(new Dimension(450, 240));
		setMinimumSize(new Dimension(300, 120));
	}

	private void initLayout() {
		layout.putConstraint(SpringLayout.NORTH, protLabel, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, protLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, protocols, 10, SpringLayout.SOUTH, protLabel);
		layout.putConstraint(SpringLayout.WEST, protocols, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, protocols, 75 + 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, domainLabel, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, domainLabel, 10, SpringLayout.EAST, protocols);

		layout.putConstraint(SpringLayout.NORTH, hostField, 10, SpringLayout.SOUTH, domainLabel);
		layout.putConstraint(SpringLayout.WEST, hostField, 10, SpringLayout.EAST, protocols);
		layout.putConstraint(SpringLayout.EAST, hostField, 350 + 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, portLabel, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, portLabel, 0, SpringLayout.WEST, portField);

		layout.putConstraint(SpringLayout.NORTH, portField, 0, SpringLayout.NORTH, hostField);
		layout.putConstraint(SpringLayout.WEST, portField, 10, SpringLayout.EAST, hostField);
		layout.putConstraint(SpringLayout.EAST, portField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, pathLabel, 10, SpringLayout.SOUTH, hostField);
		layout.putConstraint(SpringLayout.WEST, pathLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, pathField, 5, SpringLayout.SOUTH, pathLabel);
		layout.putConstraint(SpringLayout.WEST, pathField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, pathField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, hostHeaderLabel, 5, SpringLayout.SOUTH, pathField);
		layout.putConstraint(SpringLayout.WEST, hostHeaderLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, hostHeaderField, 5, SpringLayout.SOUTH, hostHeaderLabel);
		layout.putConstraint(SpringLayout.WEST, hostHeaderField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, hostHeaderField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, cacheCheckbox, 5, SpringLayout.SOUTH, hostHeaderField);
		layout.putConstraint(SpringLayout.WEST, cacheCheckbox, 0, SpringLayout.WEST, hostHeaderLabel);

	}

	@Override
	public ProxyConfigRequest getResult() {
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
		config.setHost(StringVariable.builder().originalValue(host).build());
		config.setPort(port);
		config.setPath(StringVariable.builder().originalValue(path).build());
		config.setHostHeader(hostHeaderField.getText());
		config.setEnableCache(cacheCheckbox.isSelected());
		config.setProtocol(String.valueOf(protocols.getSelectedItem()));

		return config;
	}

	@Override
	public void setValue(ProxyConfigRequest config) {
		if (config == null) {
			config = ProxyConfigRequest.builder().build();
		}

		if (config instanceof ProxyConfigRequest) {
			ProxyConfigRequest configRequest = (ProxyConfigRequest) config;
			String sport = "";
			if (configRequest.getPort() > 0)
				sport = String.valueOf(configRequest.getPort());

			for (int i = 0; i < protocolList.length; i++) {
				String v = protocolList[i];
				if (v.equalsIgnoreCase(configRequest.getProtocol())) {
					this.protocols.setSelectedIndex(i);
				}
			}

			this.hostField.setText(configRequest.getHost().getOriginalValue());
			this.portField.setText(sport);
			this.hostHeaderField.setText(configRequest.getHostHeader());
		}

		this.pathField.setText(config.getPath().getOriginalValue());
		this.cacheCheckbox.setSelected(config.isEnableCache());

	}
}
