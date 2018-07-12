package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.config.model.StringVariable;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.ui.SwingUtils;

public class FileProxyEditorPanel extends JPanel implements EditorPanel<ProxyConfigFile> {
	private static final long serialVersionUID = 5379224168584631339L;
	private static final String[] protocolList = { "http", "https" };

	private SpringLayout layout;
	private JLabel pathLabel;
	private JTextField pathField;
	private JLabel hostHeaderLabel;
	private JTextField hostHeaderField;
	private JLabel filePathLabel;
	private JTextField filePathField;

	public FileProxyEditorPanel() {
		layout = new SpringLayout();
		setLayout(layout);

		pathLabel = new JLabel("Path");
		pathField = SwingUtils.newJTextField();
		add(pathLabel);
		add(pathField);

		hostHeaderLabel = new JLabel("Host Header");
		hostHeaderField = SwingUtils.newJTextField();
		add(hostHeaderLabel);
		add(hostHeaderField);

		filePathLabel = new JLabel("File Path");
		filePathField = SwingUtils.newJTextField();
		add(filePathLabel);
		add(filePathField);


		initLayout();
		setPreferredSize(new Dimension(450, 240));
		setMinimumSize(new Dimension(300, 120));
	}

	private void initLayout() {
		layout.putConstraint(SpringLayout.NORTH, pathLabel, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, pathLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, pathField, 5, SpringLayout.SOUTH, pathLabel);
		layout.putConstraint(SpringLayout.WEST, pathField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, pathField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, hostHeaderLabel, 5, SpringLayout.SOUTH, pathField);
		layout.putConstraint(SpringLayout.WEST, hostHeaderLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, hostHeaderField, 5, SpringLayout.SOUTH, hostHeaderLabel);
		layout.putConstraint(SpringLayout.WEST, hostHeaderField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, hostHeaderField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, filePathLabel, 5, SpringLayout.SOUTH, hostHeaderField);
		layout.putConstraint(SpringLayout.WEST, filePathLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, filePathField, 5, SpringLayout.SOUTH, filePathLabel);
		layout.putConstraint(SpringLayout.WEST, filePathField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, filePathField, -10, SpringLayout.EAST, this);

	}

	@Override
	public ProxyConfigFile getResult() {
		String path = pathField.getText();

		ProxyConfigFile config = new ProxyConfigFile();
//		config.setHost(StringVariable.builder().originalValue(host).build());
//		config.setPort(port);
		config.setPath(StringVariable.builder().originalValue(path).build());
//		config.setHostHeader(hostHeaderField.getText());
//		config.setEnableCache(cacheCheckbox.isSelected());
//		config.setProtocol(String.valueOf(protocols.getSelectedItem()));

		return config;
	}

	@Override
	public void setValue(ProxyConfigFile config) {
		if (config == null) {
			config = ProxyConfigFile.builder().build();
		}

		this.pathField.setText(config.getPath().getOriginalValue());

	}
}
