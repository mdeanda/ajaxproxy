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
	private JLabel filePathLabel;
	private JTextField filePathField;
	private JLabel filterPathLabel;
	private JTextField filterPathField;

	public FileProxyEditorPanel() {
		layout = new SpringLayout();
		setLayout(layout);

		pathLabel = new JLabel("Path");
		pathField = SwingUtils.newJTextField();
		add(pathLabel);
		add(pathField);

		filePathLabel = new JLabel("File Path");
		filePathField = SwingUtils.newJTextField();
		add(filePathLabel);
		add(filePathField);

		filterPathLabel = new JLabel("Filter Path");
		filterPathField = SwingUtils.newJTextField();
		add(filterPathLabel);
		add(filterPathField);

		initLayout();
		setPreferredSize(new Dimension(450, 220));
		setMinimumSize(new Dimension(300, 140));
	}

	private void initLayout() {
		layout.putConstraint(SpringLayout.NORTH, pathLabel, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, pathLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, pathField, 5, SpringLayout.SOUTH, pathLabel);
		layout.putConstraint(SpringLayout.WEST, pathField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, pathField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, filePathLabel, 5, SpringLayout.SOUTH, pathField);
		layout.putConstraint(SpringLayout.WEST, filePathLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, filePathField, 5, SpringLayout.SOUTH, filePathLabel);
		layout.putConstraint(SpringLayout.WEST, filePathField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, filePathField, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, filterPathLabel, 5, SpringLayout.SOUTH, filePathField);
		layout.putConstraint(SpringLayout.WEST, filterPathLabel, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, filterPathField, 5, SpringLayout.SOUTH, filterPathLabel);
		layout.putConstraint(SpringLayout.WEST, filterPathField, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, filterPathField, -10, SpringLayout.EAST, this);

	}

	@Override
	public ProxyConfigFile getResult() {
		String path = pathField.getText();
		String basePath = filePathField.getText();

		ProxyConfigFile config = new ProxyConfigFile();
		config.setPath(StringVariable.builder().originalValue(path).build());
		config.setBasePath(StringVariable.builder().originalValue(basePath).build());
		config.setFilterPath(filterPathField.getText());

		return config;
	}

	@Override
	public void setValue(ProxyConfigFile config) {
		if (config == null) {
			config = ProxyConfigFile.builder().build();
		}

		this.pathField.setText(config.getPath().getOriginalValue());
		this.filePathField.setText(config.getBasePath().getOriginalValue());
		this.filterPathField.setText(config.getFilterPath());

	}
}
