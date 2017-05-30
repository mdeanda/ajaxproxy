package com.thedeanda.ajaxproxy.ui.logger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessage;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
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
		
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setBorder(new BottomBorder());

		JLabel lbl = new JLabel("Path to JavaScript");
		panel.add(lbl);

		JTextField loggerPath = new JTextField("/logger");
		SwingUtils.prepJTextField(loggerPath);
		loggerPath.setToolTipText("path to logger");
		panel.add(loggerPath);

		
		JButton clearBtn = new JButton("Clear");
		panel.add(clearBtn);
		clearBtn.addActionListener(new ActionListener()  {
			@Override
			public void actionPerformed(ActionEvent e) {
				loggerTableModel.clear();
			}
		});
		
		JButton helpBtn = new JButton("?");
		panel.add(helpBtn);

		layout.putConstraint(SpringLayout.NORTH, helpBtn, 20, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, helpBtn, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, panel, 10, SpringLayout.SOUTH, helpBtn);

		layout.putConstraint(SpringLayout.WEST, lbl, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.BASELINE, lbl, 0, SpringLayout.BASELINE, helpBtn);

		layout.putConstraint(SpringLayout.WEST, loggerPath, 10, SpringLayout.EAST, lbl);
		layout.putConstraint(SpringLayout.EAST, loggerPath, 250, SpringLayout.WEST, loggerPath);
		layout.putConstraint(SpringLayout.BASELINE, loggerPath, 0, SpringLayout.BASELINE, helpBtn);

		layout.putConstraint(SpringLayout.WEST, clearBtn, 10, SpringLayout.EAST, loggerPath);
		layout.putConstraint(SpringLayout.BASELINE, clearBtn, 0, SpringLayout.BASELINE, helpBtn);

				//return panel;
		return filterPanel;
	}

	private JPanel initLeftPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		LoggerColumnModel lcm = new LoggerColumnModel();
		JTable table = new JTable(loggerTableModel, lcm);
		JScrollPane scroll = new JScrollPane(table);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;

				int index = e.getLastIndex();
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

	public void setProxy(AjaxProxy proxy) {
		if (proxy != null) {
			proxy.addLoggerMessageListener(loggerTableModel);
		}
	}

	private void itemSelected(LoggerMessage message) {
		loggerMessagePanel.setLoggerMessage(message);
	}

}
