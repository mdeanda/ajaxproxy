package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.SwingUtils;

public class ProxyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int COLUMN_WIDTH = 150;
	private JTable proxyTable;
	private SpringLayout layout;
	private JTextField domainField;
	private JTextField portField;
	private JTextField pathField;
	private JComboBox<?> newProxyField;
	private JLabel domainLabel;
	private JLabel portLabel;
	private JLabel pathLabel;
	private JLabel newProxyLabel;
	private JScrollPane scroll;
	private JButton btn;
	private ProxyTableModel proxyModel;

	public ProxyPanel(final SettingsChangedListener listener,
			final ProxyTableModel proxyModel) {
		layout = new SpringLayout();
		setLayout(layout);
		this.proxyModel = proxyModel;
		proxyTable = new JTable(proxyModel);
		proxyTable.setColumnModel(new ProxyColumnModel());
		proxyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final ListSelectionModel cellSelectionModel = proxyTable
				.getSelectionModel();
		cellSelectionModel
				.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							startEdit();
						}
					}

				});
		scroll = new JScrollPane(proxyTable);

		add(scroll);

		proxyModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				listener.settingsChanged();
				listener.restartRequired();
			}
		});

		domainLabel = new JLabel("Domain");
		domainField = SwingUtils.newJTextField();
		add(domainLabel);
		add(domainField);

		portLabel = new JLabel("Port");
		portField = SwingUtils.newJTextField();
		add(portLabel);
		add(portField);

		pathLabel = new JLabel("Path");
		pathField = SwingUtils.newJTextField();
		add(pathLabel);
		add(pathField);

		newProxyLabel = new JLabel("New Proxy");
		newProxyField = SwingUtils.newJComboBox(new Boolean[] { Boolean.FALSE,
				Boolean.TRUE });

		add(newProxyLabel);
		add(newProxyField);

		btn = new JButton("Ok");
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commitEdit();
			}
		});
		add(btn);

		initLayout();
	}

	private void startEdit() {
		int row = proxyTable.getSelectedRow();
		String val = (String) proxyModel.getValueAt(row, 0);
		domainField.setText(val);

		Integer num = (Integer) proxyModel.getValueAt(row, 1);
		portField.setText(String.valueOf(num));

		val = (String) proxyModel.getValueAt(row, 2);
		pathField.setText(val);

		Boolean bol = (Boolean) proxyModel.getValueAt(row, 3);
		newProxyField.getSelectedIndex();
		// newProxyField.setText(val);
	}

	private void commitEdit() {
		int row = proxyTable.getSelectedRow();
		if (row < 0) {
			row = proxyModel.getRowCount() - 1;
		}
		int port = 0;
		try {
			if (!StringUtils.isBlank(portField.getText()))
				port = Integer.parseInt(portField.getText());
		} catch (NumberFormatException nfe) {
			return;
		}
		proxyModel.setValue(row, domainField.getText(), port,
				pathField.getText(), newProxyField.getSelectedItem());
		proxyTable.changeSelection(row, 0, false, true);
	}

	private void initLayout() {
		// table
		layout.putConstraint(SpringLayout.NORTH, scroll, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10,
				SpringLayout.NORTH, domainLabel);

		JLabel[] labels = new JLabel[] { domainLabel, portLabel, pathLabel,
				newProxyLabel, null };
		Component[] fields = new Component[] { domainField, portField,
				pathField, newProxyField, btn };
		int[] cols = new int[] { 250, 70, 250, 150, 60 };

		for (int i = cols.length - 1; i >= 0; i--) {
			JLabel lbl = labels[i];
			Component fld = fields[i];
			if (i == 0) {
				layout.putConstraint(SpringLayout.SOUTH, fld, -10,
						SpringLayout.SOUTH, this);

				layout.putConstraint(SpringLayout.WEST, fld, 10,
						SpringLayout.WEST, this);
				if (lbl != null)
					layout.putConstraint(SpringLayout.WEST, lbl, 10,
							SpringLayout.WEST, this);
			} else {
				layout.putConstraint(SpringLayout.SOUTH, fld, 0,
						SpringLayout.SOUTH, fields[0]);

				layout.putConstraint(SpringLayout.NORTH, fld, 0,
						SpringLayout.NORTH, fields[0]);

				layout.putConstraint(SpringLayout.WEST, fld, 10 - cols[i],
						SpringLayout.EAST, fld);
				if (lbl != null)
					layout.putConstraint(SpringLayout.WEST, lbl, 10 - cols[i],
							SpringLayout.EAST, fld);
			}

			if (i == fields.length - 1) {
				layout.putConstraint(SpringLayout.EAST, fld, -10,
						SpringLayout.EAST, this);
				if (lbl != null)
					layout.putConstraint(SpringLayout.EAST, lbl, -10,
							SpringLayout.EAST, this);
			} else {
				layout.putConstraint(SpringLayout.EAST, fld, -10,
						SpringLayout.WEST, fields[i + 1]);
				if (lbl != null)
					layout.putConstraint(SpringLayout.EAST, lbl, -10,
							SpringLayout.WEST, fields[i + 1]);
			}

			if (lbl != null)
				layout.putConstraint(SpringLayout.SOUTH, lbl, -5,
						SpringLayout.NORTH, fld);

		}
		layout.putConstraint(SpringLayout.NORTH, btn, 0, SpringLayout.NORTH,
				domainField);

	}
}
