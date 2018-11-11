package com.thedeanda.ajaxproxy.ui.rest;

import com.thedeanda.ajaxproxy.config.model.proxy.HttpHeader;
import com.thedeanda.ajaxproxy.http.HttpClient;
import com.thedeanda.ajaxproxy.http.HttpClient.RequestMethod;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
import com.thedeanda.ajaxproxy.ui.viewer.RequestViewer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RestClientPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(RestClientPanel.class);

	private static final int CONNECT_TIMEOUT = 5000;
	private static final int CONNECTION_REQUEST_TIMEOUT = 5000;
	private static final int SOCKET_TIMEOUT = 300000;

	private JTextField urlField;
	private JTextArea headersField;
	private JTextArea inputField;
	private JComboBox<String> addHeaderCombo;
	private JComboBox<String> methodCombo;
	private JButton submitButton;
	private HttpClient httpClient;
	private RequestViewer outputPanel;
	private RequestListener listener;
	private HistoryListModel historyModel;

	public RestClientPanel() {
		httpClient = new HttpClient(CONNECT_TIMEOUT, CONNECTION_REQUEST_TIMEOUT, SOCKET_TIMEOUT);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JPanel urlPanel = initUrlPanel();
		urlPanel.setBorder(new BottomBorder());
		add(urlPanel);

		JSplitPane historySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		historySplit.setDividerLocation(0);
		SwingUtils.flattenSplitPane(historySplit);
		add(historySplit);

		initHistoryPanel(historySplit);

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		SwingUtils.flattenSplitPane(mainSplit);
		// add(mainSplit);
		historySplit.setRightComponent(mainSplit);

		JPanel leftPanel = initLeftPanel();
		mainSplit.setLeftComponent(leftPanel);

		outputPanel = new RequestViewer();
		mainSplit.setRightComponent(outputPanel);
		mainSplit.setDividerLocation(400);

		layout.putConstraint(SpringLayout.NORTH, urlPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, urlPanel, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, urlPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.SOUTH, urlPanel, 50, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.EAST, historySplit, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, historySplit, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.SOUTH, historySplit, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, historySplit, 0, SpringLayout.SOUTH, urlPanel);
	}

	private void initHistoryPanel(JSplitPane historySplit) {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		historyModel = new HistoryListModel();

		JList<HistoryItem> list = new JList<>(historyModel);
		JScrollPane historyScroll = new JScrollPane(list);
		panel.add(historyScroll);

		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;

				JList<?> list = (JList<?>) e.getSource();

				int index = list.getSelectedIndex();
				if (index < 0)
					return;

				HistoryItem item = historyModel.getElementAt(index);
				historyItemSelected(item);
			}
		});

		layout.putConstraint(SpringLayout.NORTH, historyScroll, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, historyScroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, historyScroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, historyScroll, -10, SpringLayout.SOUTH, panel);

		historySplit.setLeftComponent(panel);
	}

	private void historyItemSelected(HistoryItem item) {
		setUrl(item.getUrl());
		setHeaders(item.getHeaders());
		setMethod(item.getMethod());

		// possibly support byte input later
		if (item.getInput() != null) {
			setInput(new String(item.getInput(), Charset.forName("UTF-8")));
		} else {
			setInput("");
		}
	}

	private JPanel initUrlPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setMinimumSize(new Dimension(200, 50));

		JLabel urlLabel = SwingUtils.newJLabel("URL");
		urlField = SwingUtils.newJTextField();
		panel.add(urlLabel);

		urlField = SwingUtils.newJTextField();
		panel.add(urlField);

		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		panel.add(submitButton);

		JComboBox<String> methods = createMethodDropDown();
		methodCombo = methods;
		panel.add(methods);

		// url label
		layout.putConstraint(SpringLayout.WEST, urlLabel, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, urlLabel, 0, SpringLayout.VERTICAL_CENTER, methods);

		// submit button
		layout.putConstraint(SpringLayout.EAST, submitButton, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, submitButton, 0, SpringLayout.VERTICAL_CENTER, panel);

		// method list
		layout.putConstraint(SpringLayout.EAST, methods, -10, SpringLayout.WEST, submitButton);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, methods, 0, SpringLayout.VERTICAL_CENTER, panel);

		// input field
		layout.putConstraint(SpringLayout.WEST, urlField, 10, SpringLayout.EAST, urlLabel);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, urlField, 0, SpringLayout.VERTICAL_CENTER, methods);
		layout.putConstraint(SpringLayout.EAST, urlField, -10, SpringLayout.WEST, methods);

		return panel;
	}

	public void setDefaultButton() {
		getRootPane().setDefaultButton(submitButton);
	}

	private JPanel initLeftPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setMinimumSize(new Dimension(200, 350));

		JSplitPane split = initLeftSplit();
		panel.add(split);

		// split
		layout.putConstraint(SpringLayout.NORTH, split, 24, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, split, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.SOUTH, split, -10, SpringLayout.SOUTH, panel);

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
		layout.putConstraint(SpringLayout.WEST, inputLabel, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, inputLabel, 5, SpringLayout.NORTH, panel);

		// input area
		layout.putConstraint(SpringLayout.WEST, inputScroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, inputScroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, inputScroll, 10, SpringLayout.SOUTH, inputLabel);
		layout.putConstraint(SpringLayout.SOUTH, inputScroll, 0, SpringLayout.SOUTH, panel);

		return panel;
	}

	private JPanel initHeadersPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JLabel headersLabel = SwingUtils.newJLabel("Request Headers");
		headersField = SwingUtils.newJTextArea();

		JScrollPane headersScroll = new JScrollPane(headersField);

		JComboBox<String> dropDown = createAddHeaderDropDown();
		panel.add(dropDown);

		panel.add(headersLabel);
		panel.add(headersScroll);

		// headers label
		layout.putConstraint(SpringLayout.WEST, headersLabel, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, headersLabel, 0, SpringLayout.NORTH, panel);

		// add header drop down
		layout.putConstraint(SpringLayout.WEST, dropDown, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, dropDown, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, dropDown, 10, SpringLayout.SOUTH, headersLabel);

		// headers field
		layout.putConstraint(SpringLayout.NORTH, headersScroll, 10, SpringLayout.SOUTH, dropDown);
		layout.putConstraint(SpringLayout.WEST, headersScroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, headersScroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, headersScroll, -5, SpringLayout.SOUTH, panel);

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
		JComboBox<String> methodCombo = new JComboBox<>(stmp);
		methodCombo.addActionListener(this);
		return methodCombo;
	}

	private String[] getAddHeaderOptions() {
		String[] lines = null;
		InputStream is = getClass().getResourceAsStream("predefined-headers.txt");
		if (is != null) {
			try {
				List<String> tmp = IOUtils.readLines(is);
				tmp.add(0, "");
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
				headersField.setText(addHeader(headersField.getText(), hdrToAdd));
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

		final RequestMethod method = RequestMethod.valueOf((String) methodCombo.getSelectedItem());
		final String url = urlField.getText();
		final String headers = headersField.getText();
		final byte[] input = inputField.getText().getBytes();

		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {

				RequestListener[] listeners = null;
				if (listener != null) {
					listeners = new RequestListener[2];
					listeners[1] = listener;
				} else {
					listeners = new RequestListener[1];
				}
				listeners[0] = outputPanel;

				try {
					log.info("making request");
					List<HttpHeader> headerList = HttpHeader.fromString(headers);
					httpClient.makeRequest(method, url, headerList, input, listeners);
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

	public String getUrl() {
		return urlField.getText();
	}

	public String getInput() {
		return inputField.getText();
	}

	public String getHeaders() {
		return headersField.getText();
	}

	public String getMethod() {
		return (String) methodCombo.getSelectedItem();
	}

	public void saveCurrent() {
		String url = getUrl();

		if (StringUtils.isBlank(url)) {
			return;
		}

		String name = url;
		// TODO: prmopt for name

		HistoryItem item = new HistoryItem();
		item.setHeaders(getHeaders());
		item.setUrl(url);
		item.setInput(getInput().getBytes());
		item.setMethod(getMethod());
		item.setName(name);
		HistoryItemService.get().save(item);

		this.historyModel.reload();
	}
}
