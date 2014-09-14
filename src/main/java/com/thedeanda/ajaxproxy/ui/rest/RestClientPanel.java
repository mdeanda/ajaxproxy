package com.thedeanda.ajaxproxy.ui.rest;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.thedeanda.ajaxproxy.ui.SwingUtils;

public class RestClientPanel extends JPanel {
	private JTextField urlField;
	private JTextArea headersField;
	private JTextArea inputField;
	private JTextArea outputField;

	public RestClientPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JLabel urlLabel = SwingUtils.newJLabel("Request URL");
		urlField = SwingUtils.newJTextField();

		JLabel headersLabel = SwingUtils.newJLabel("Headers");
		headersField = SwingUtils.newJTextArea();

		JScrollPane headersScroll = new JScrollPane(headersField);
		JSplitPane split = initSplit();

		add(urlLabel);
		add(urlField);
		add(headersLabel);
		add(headersScroll);
		add(split);

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

		// headers field
		layout.putConstraint(SpringLayout.NORTH, headersScroll, 10,
				SpringLayout.SOUTH, headersLabel);
		layout.putConstraint(SpringLayout.WEST, headersScroll, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, headersScroll, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, headersScroll, 90,
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

	private JSplitPane initSplit() {

		outputField = SwingUtils.newJTextArea();
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
		panel.setLayout(new BorderLayout());

		inputField = SwingUtils.newJTextArea();
		JScrollPane inputScroll = new JScrollPane(inputField);
		panel.add(BorderLayout.CENTER, inputScroll);
		
		panel.add(BorderLayout.NORTH, SwingUtils.newJLabel("Input"));

		return panel;
	}
}
