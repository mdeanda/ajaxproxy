package com.thedeanda.ajaxproxy.ui.rest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.ResourcePanel;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.viewer.RequestViewer;

public class RestClientPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(RestClientPanel.class);

	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String[] METHODS = new String[] { METHOD_GET,
			METHOD_POST };

	private JTextField urlField;
	private JTextArea headersField;
	private JTextArea inputField;
	private JTextArea outputField;
	private JComboBox<String> addHeaderCombo;
	private JComboBox<String> methodCombo;
	private JButton submitButton;
	private HttpClient httpClient;
	private RequestViewer outputPanel;

	public RestClientPanel() {
		httpClient = new HttpClient();

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JLabel urlLabel = SwingUtils.newJLabel("Request URL");
		urlField = SwingUtils.newJTextField();

		JLabel headersLabel = SwingUtils.newJLabel("Headers");
		headersField = SwingUtils.newJTextArea();

		JScrollPane headersScroll = new JScrollPane(headersField);
		JSplitPane split = initSplit();

		JComboBox<String> dropDown = createAddHeaderDropDown();
		JComboBox<String> methods = createMethodDropDown();

		add(urlLabel);
		add(methods);
		add(urlField);
		add(headersLabel);
		add(headersScroll);
		add(split);
		add(dropDown);

		// methods
		layout.putConstraint(SpringLayout.EAST, methods, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, methods, 20,
				SpringLayout.NORTH, this);

		// url label
		layout.putConstraint(SpringLayout.WEST, urlLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, urlLabel, 0,
				SpringLayout.VERTICAL_CENTER, methods);

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
		addHeaderCombo.setPreferredSize(new Dimension(250, 25));
		addHeaderCombo.addActionListener(this);
		return addHeaderCombo;
	}

	private JComboBox<String> createMethodDropDown() {
		methodCombo = new JComboBox<String>(METHODS);
		methodCombo.addActionListener(this);
		return methodCombo;
	}

	private JSplitPane initSplit() {

		outputField = SwingUtils.newJTextArea();
		outputField.setBackground(new Color(250, 250, 250));
		JScrollPane outputScroll = new JScrollPane(outputField);

		outputPanel = new RequestViewer();

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(initInputPanel());
		split.setBottomComponent(outputPanel);
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

		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		panel.add(submitButton);

		// label
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH,
				panel);

		// submit button
		layout.putConstraint(SpringLayout.EAST, submitButton, 0,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, submitButton, 0,
				SpringLayout.SOUTH, panel);

		// input area
		layout.putConstraint(SpringLayout.WEST, inputScroll, 0,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, inputScroll, 0,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, inputScroll, 10,
				SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.SOUTH, inputScroll, -10,
				SpringLayout.NORTH, submitButton);

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
		} else if (e.getSource() == submitButton) {
			handleSubmit();
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

	private void handleSubmit() {
		log.info("handle submit!");

		final RequestMethod method = RequestMethod.valueOf((String) methodCombo
				.getSelectedItem());
		final String url = urlField.getText();
		final String headers = headersField.getText();
		final byte[] input = inputField.getText().getBytes();
		outputField.setText("");

		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {

				try {
					log.info("making request");
					httpClient.makeRequest(method, url, headers, input, outputPanel);
					/*
					httpClient.makeRequest(method, url, headers, input,
							new RequestListener() {
								@Override
								public void requestComplete(UUID id,
										int status, Header[] headers,
										byte[] data) {

									StringWriter writer = new StringWriter();
									ByteArrayInputStream is = new ByteArrayInputStream(
											data);
									try {
										IOUtils.copy(is, writer);
									} catch (IOException e) {
										log.warn(e.getMessage(), e);
									}

									outputField.setText(writer.toString());
								}

								@Override
								public void newRequest(UUID id, URL url,
										Header[] requestHeaders, byte[] data) {
									// TODO Auto-generated method stub

								}

							});*/
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}
		});
	}
}
