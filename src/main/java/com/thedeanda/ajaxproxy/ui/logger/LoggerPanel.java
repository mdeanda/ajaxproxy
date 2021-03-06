package com.thedeanda.ajaxproxy.ui.logger;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessage;
import com.thedeanda.ajaxproxy.ui.util.SwingUtils;
import com.thedeanda.javajson.JsonObject;

public class LoggerPanel extends JPanel {
	private static final long serialVersionUID = -2124288848339204131L;

	private LoggerTableModel loggerTableModel = new LoggerTableModel();

	private LoggerMessagePanel loggerMessagePanel;
	private FilterPanel filterPanel;

	public LoggerPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JPanel topPanel = initTopPanel();
		add(topPanel);

		loggerMessagePanel = new LoggerMessagePanel();

		JPanel leftPanel = initLeftPanel();
		JPanel rightPanel = loggerMessagePanel;

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(leftPanel);
		split.setRightComponent(rightPanel);
		split.setDividerLocation(650);
		split.setBorder(BorderFactory.createEmptyBorder());
		SwingUtils.flattenSplitPane(split);
		add(split);

		layout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, topPanel, 60, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, split, 0, SpringLayout.SOUTH, topPanel);
		layout.putConstraint(SpringLayout.SOUTH, split, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, split, 0, SpringLayout.EAST, this);

	}

	private JPanel initTopPanel() {
		this.filterPanel = new FilterPanel();
		filterPanel.setModel(loggerTableModel);

		return filterPanel;
	}

	private JPanel initLeftPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		LoggerColumnModel lcm = new LoggerColumnModel();
		final JTable table = new JTable(loggerTableModel, lcm);
		JScrollPane scroll = new JScrollPane(table);

		table.getColumnModel().setColumnSelectionAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				int index = table.getSelectedRow();
				LoggerMessage message = loggerTableModel.getMessage(index);
				itemSelected(message);
			}
		});

		panel.add(scroll, BorderLayout.CENTER);

		layout.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, panel);

		return panel;
	}

	public void updateConfig(JsonObject json) {
		// TODO Auto-generated method stub
		// TODO: set request path

	}

	public void setProxy(AjaxProxyServer proxy) {
		if (proxy != null) {
			proxy.addLoggerMessageListener(loggerTableModel);
		}
	}

	private void itemSelected(LoggerMessage message) {
		loggerMessagePanel.setLoggerMessage(message);
	}

}
