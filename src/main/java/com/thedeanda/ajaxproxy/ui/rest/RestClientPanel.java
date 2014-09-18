package com.thedeanda.ajaxproxy.ui.rest;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.SwingUtils;

public class RestClientPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(RestClientPanel.class);
	private JTextField urlField;
	private JTextArea headersField;
	private JTextArea inputField;
	private JTextArea outputField;
	private JComboBox<String> addHeaderCombo;

	public RestClientPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JLabel urlLabel = SwingUtils.newJLabel("Request URL");
		urlField = SwingUtils.newJTextField();

		JLabel headersLabel = SwingUtils.newJLabel("Headers");
		headersField = SwingUtils.newJTextArea();

		JScrollPane headersScroll = new JScrollPane(headersField);
		JSplitPane split = initSplit();

		JComboBox<String> dropDown = createAddHeaderDropDown();

		add(urlLabel);
		add(urlField);
		add(headersLabel);
		add(headersScroll);
		add(split);
		add(dropDown);

		// url label
		layout.putConstraint(SpringLayout.WEST, urlLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, urlLabel, 20,
				SpringLayout.NORTH, this);

		// url field
		layout.putConstraint(SpringLayout.NORTH, urlField, 10,
				SpringLayout.SOUTH, urlLabel);
		layout.putConstraint(SpringLayout.EAST, urlField, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, urlField, 10,
				SpringLayout.WEST, this);

		// headers label
		layout.putConstraint(SpringLayout.WEST, headersLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, headersLabel, 20,
				SpringLayout.SOUTH, urlField);

		// add header drop down
		layout.putConstraint(SpringLayout.EAST, dropDown, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, dropDown, 0,
				SpringLayout.VERTICAL_CENTER, headersLabel);

		// headers field
		layout.putConstraint(SpringLayout.NORTH, headersScroll, 10,
				SpringLayout.SOUTH, headersLabel);
		layout.putConstraint(SpringLayout.WEST, headersScroll, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, headersScroll, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, headersScroll, 115,
				SpringLayout.NORTH, headersLabel);

		layout.putConstraint(SpringLayout.NORTH, split, 20, SpringLayout.SOUTH,
				headersScroll);
		layout.putConstraint(SpringLayout.WEST, split, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, split, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.SOUTH, split, -10,
				SpringLayout.SOUTH, this);
	}

	private JComboBox<String> createAddHeaderDropDown() {
		String[] predefinedHeaders = getAddHeaderOptions();

		addHeaderCombo = new JComboBox<String>(predefinedHeaders);
		addHeaderCombo.addActionListener(this);
		return addHeaderCombo;
	}

	private JSplitPane initSplit() {

		outputField = SwingUtils.newJTextArea();
		outputField.setBackground(new Color(250, 250, 250));
		JScrollPane outputScroll = new JScrollPane(outputField);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(initInputPanel());
		split.setBottomComponent(outputScroll);
		split.setDividerLocation(170);
		split.setBorder(BorderFactory.createEmptyBorder());
		SwingUtils.flattenSplitPane(split);
		add(split);

		return split;
	}

	private JPanel initInputPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JLabel label = SwingUtils.newJLabel("Input");
		panel.add(label);

		inputField = SwingUtils.newJTextArea();
		JScrollPane inputScroll = new JScrollPane(inputField);
		panel.add(inputScroll);

		// *
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH,
				panel);
		// */

		layout.putConstraint(SpringLayout.WEST, inputScroll, 0,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, inputScroll, 0,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, inputScroll, 10,
				SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.SOUTH, inputScroll, 0,
				SpringLayout.SOUTH, panel);

		// panel.setPreferredSize(new Dimension(500, 150));
		return panel;
	}

	private String[] getAddHeaderOptions() {
		String[] lines = null;
		InputStream is = getClass().getResourceAsStream(
				"predefined-headers.txt");
		if (is != null) {
			try {
				List<String> tmp = IOUtils.readLines(is);
				lines = new String[tmp.size()];
				tmp.toArray(lines);
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}

		}
		return lines;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addHeaderCombo) {
			String hdrToAdd = (String) addHeaderCombo.getSelectedItem();
			if (!StringUtils.isBlank(hdrToAdd)) {
				headersField
						.setText(addHeader(headersField.getText(), hdrToAdd));
				addHeaderCombo.setSelectedIndex(0);
			}
		}

	}

	private String addHeader(String currentHeaders, String newHeader) {
		log.debug("input: {}\n\n add: {}", currentHeaders, newHeader);
		if (currentHeaders == null) {
			currentHeaders = "";
		}

		if (StringUtils.isBlank(newHeader)) {
			log.debug("add empty header, abort");
			return currentHeaders;
		}

		String[] parts = newHeader.split(":", 2);
		if (parts.length != 2) {
			log.debug("parts length incorrect, abort");
			return currentHeaders;
		}

		String newKey = parts[0];
		// String newVal = parts[1];
		String[] lines = currentHeaders.split("\n");
		StringBuilder output = new StringBuilder();
		for (String line : lines) {
			if (StringUtils.isBlank(line))
				continue;

			String[] lineParts = line.split(":", 2);
			if (lineParts.length == 2) {
				if (!lineParts[0].equalsIgnoreCase(newKey)) {
					output.append(line);
					output.append("\n");
				}
			} else {
				// don't mess with headers missing ':'
				output.append(line);
				output.append("\n");
			}
		}
		output.append(newHeader);
		output.append("\n");

		log.debug("return: {}", output);
		return output.toString();
	}
}
