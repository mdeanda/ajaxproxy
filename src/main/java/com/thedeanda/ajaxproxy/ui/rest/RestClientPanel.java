package com.thedeanda.ajaxproxy.ui.rest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.viewer.RequestViewer;

public class RestClientPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(RestClientPanel.class);

	private JTextField urlField;
	private JTextArea headersField;
	private JTextArea inputField;
	private JComboBox<String> addHeaderCombo;
	private JComboBox<String> methodCombo;
	private JButton submitButton;
	private HttpClient httpClient;
	private RequestViewer outputPanel;
	private RequestListener listener;

	public RestClientPanel() {
		httpClient = new HttpClient();

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		SwingUtils.flattenSplitPane(mainSplit);
		setLayout(new BorderLayout());
		add(mainSplit);

		JPanel leftPanel = initLeftPanel();
		mainSplit.setLeftComponent(leftPanel);

		outputPanel = new RequestViewer();
		mainSplit.setRightComponent(outputPanel);
		mainSplit.setDividerLocation(400);

	}

	public void setDefaultButton() {
		getRootPane().setDefaultButton(submitButton);
	}

	private JPanel initLeftPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setMinimumSize(new Dimension(200, 350));

		JLabel urlLabel = SwingUtils.newJLabel("Request URL");
		urlField = SwingUtils.newJTextField();

		JComboBox<String> methods = createMethodDropDown();
		panel.add(methods);

		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		panel.add(submitButton);

		panel.add(urlLabel);
		panel.add(urlField);

		JSplitPane split = initLeftSplit();
		panel.add(split);

		// methods
		layout.putConstraint(SpringLayout.EAST, methods, -10,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, methods, 20,
				SpringLayout.NORTH, panel);

		// url label
		layout.putConstraint(SpringLayout.WEST, urlLabel, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, urlLabel, 0,
				SpringLayout.VERTICAL_CENTER, methods);

		// url field
		layout.putConstraint(SpringLayout.NORTH, urlField, 10,
				SpringLayout.SOUTH, urlLabel);
		layout.putConstraint(SpringLayout.EAST, urlField, -10,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.WEST, urlField, 10,
				SpringLayout.WEST, panel);

		// split
		layout.putConstraint(SpringLayout.NORTH, split, 10, SpringLayout.SOUTH,
				urlField);
		layout.putConstraint(SpringLayout.EAST, split, 0, SpringLayout.EAST,
				panel);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.SOUTH, split, -10,
				SpringLayout.NORTH, submitButton);

		// submit button
		layout.putConstraint(SpringLayout.EAST, submitButton, -10,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, submitButton, -10,
				SpringLayout.SOUTH, panel);

		return panel;
	}

	private JSplitPane initLeftSplit() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		SwingUtils.flattenSplitPane(split);

		split.setTopComponent(initHeadersPanel());
		split.setBottomComponent(initInputPanel());
		split.setDividerLocation(150);

		return split;
	}

	private JPanel initInputPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JLabel inputLabel = SwingUtils.newJLabel("Input");
		panel.add(inputLabel);

		inputField = SwingUtils.newJTextArea();
		inputField.setWrapStyleWord(true);
		inputField.setLineWrap(true);
		JScrollPane inputScroll = new JScrollPane(inputField);
		panel.add(inputScroll);

		// input label
		layout.putConstraint(SpringLayout.WEST, inputLabel, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, inputLabel, 5,
				SpringLayout.NORTH, panel);

		// input area
		layout.putConstraint(SpringLayout.WEST, inputScroll, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, inputScroll, -10,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, inputScroll, 10,
				SpringLayout.SOUTH, inputLabel);
		layout.putConstraint(SpringLayout.SOUTH, inputScroll, 0,
				SpringLayout.SOUTH, panel);

		return panel;
	}

	private JPanel initHeadersPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JLabel headersLabel = SwingUtils.newJLabel("Headers");
		headersField = SwingUtils.newJTextArea();

		JScrollPane headersScroll = new JScrollPane(headersField);

		JComboBox<String> dropDown = createAddHeaderDropDown();
		panel.add(dropDown);

		panel.add(headersLabel);
		panel.add(headersScroll);

		// headers label
		layout.putConstraint(SpringLayout.WEST, headersLabel, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, headersLabel, 0,
				SpringLayout.NORTH, panel);

		// add header drop down
		layout.putConstraint(SpringLayout.WEST, dropDown, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, dropDown, -10,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, dropDown, 10,
				SpringLayout.SOUTH, headersLabel);

		// headers field
		layout.putConstraint(SpringLayout.NORTH, headersScroll, 10,
				SpringLayout.SOUTH, dropDown);
		layout.putConstraint(SpringLayout.WEST, headersScroll, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, headersScroll, -10,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, headersScroll, -5,
				SpringLayout.SOUTH, panel);

		return panel;
	}

	private JComboBox<String> createAddHeaderDropDown() {
		String[] predefinedHeaders = getAddHeaderOptions();

		addHeaderCombo = new JComboBox<String>(predefinedHeaders);
		addHeaderCombo.setPreferredSize(new Dimension(250, 25));
		addHeaderCombo.addActionListener(this);
		return addHeaderCombo;
	}

	private JComboBox<String> createMethodDropDown() {
		List<String> tmp = new ArrayList<String>();
		for (RequestMethod rm : RequestMethod.values()) {
			tmp.add(rm.name());
		}
		String[] stmp = new String[tmp.size()];
		tmp.toArray(stmp);
		methodCombo = new JComboBox<String>(stmp);
		methodCombo.addActionListener(this);
		return methodCombo;
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

		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {

				RequestListener[] items = null;
				if (listener != null) {
					items = new RequestListener[2];
					items[1] = listener;
				} else {
					items = new RequestListener[1];
				}
				items[0] = outputPanel;

				try {
					log.info("making request");
					httpClient.makeRequest(method, url, headers, input, items);
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}
		});
	}

	public RequestListener getListener() {
		return listener;
	}

	public void setListener(RequestListener listener) {
		this.listener = listener;
	}

	public void setUrl(String url) {
		this.urlField.setText(url);
	}

	public void setHeaders(String headers) {
		this.headersField.setText(headers);
	}

	public void setInput(String input) {
		if (input == null)
			input = "";
		else
			input = input.trim();
		this.inputField.setText(input);
	}

	public void setMethod(String method) {
		methodCombo.setSelectedItem(method);
	}
}
