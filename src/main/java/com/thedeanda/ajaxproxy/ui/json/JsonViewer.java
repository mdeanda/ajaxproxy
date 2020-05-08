package com.thedeanda.ajaxproxy.ui.json;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import com.thedeanda.ajaxproxy.ui.util.SwingUtils;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ContentViewer;

public class JsonViewer extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final String CMD_FORMAT = "FORMAT";
	private final String CMD_CLEAR = "CLEAR";
	private JTextArea inputField;
	private JButton clearButton;
	private JButton submitButton;
	private JScrollPane inputScroll;
	private ContentViewer contentViewer;

	public JsonViewer() {
		JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		SwingUtils.flattenSplitPane(mainSplit);
		setLayout(new BorderLayout());
		add(mainSplit);

		JPanel topPanel = initTopPanel();
		mainSplit.setTopComponent(topPanel);

		JPanel bottomPanel = initBottomPanel();
		mainSplit.setBottomComponent(bottomPanel);

		mainSplit.setDividerLocation(150);
	}

	private JPanel initBottomPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		contentViewer = new ContentViewer();
		panel.add(contentViewer);

		layout.putConstraint(SpringLayout.NORTH, contentViewer, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, contentViewer, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.WEST, contentViewer, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.SOUTH, contentViewer, -10, SpringLayout.SOUTH, panel);

		return panel;
	}

	private JPanel initTopPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setMinimumSize(new Dimension(100, 110));

		JLabel inputLabel = SwingUtils.newJLabel("Input");
		inputField = SwingUtils.newJTextArea();
		inputField.setWrapStyleWord(true);
		inputField.setLineWrap(true);
		inputScroll = new JScrollPane(inputField);
		panel.add(inputLabel);
		panel.add(inputScroll);

		clearButton = new JButton("Clear");
		clearButton.setActionCommand(CMD_CLEAR);
		clearButton.addActionListener(this);
		panel.add(clearButton);

		submitButton = new JButton("Format");
		submitButton.setActionCommand(CMD_FORMAT);
		submitButton.addActionListener(this);
		panel.add(submitButton);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, inputLabel, 0, SpringLayout.VERTICAL_CENTER, submitButton);
		layout.putConstraint(SpringLayout.WEST, inputLabel, 10, SpringLayout.WEST, panel);

		// url field
		layout.putConstraint(SpringLayout.NORTH, inputScroll, 10, SpringLayout.SOUTH, inputLabel);
		layout.putConstraint(SpringLayout.EAST, inputScroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.WEST, inputScroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.SOUTH, inputScroll, -10, SpringLayout.SOUTH, panel);



		// submit button
		layout.putConstraint(SpringLayout.EAST, submitButton, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, submitButton, 20, SpringLayout.NORTH, panel);

		// submit button
		layout.putConstraint(SpringLayout.EAST, clearButton, -10, SpringLayout.WEST, submitButton);
		layout.putConstraint(SpringLayout.NORTH, clearButton, 20, SpringLayout.NORTH, panel);

		return panel;
	}

	public void setDefaultButton() {
		getRootPane().setDefaultButton(submitButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (CMD_FORMAT.equals(command)) {
			format();
		} else if (CMD_CLEAR.equals(command)) {
			inputField.setText("");
			contentViewer.setContent((byte[]) null);
		}
	}

	public void setText(String text) {
		inputField.setText(text);
	}

	public void format() {
		byte[] bytes = inputField.getText().getBytes();
		contentViewer.setContent(bytes);
	}
}
