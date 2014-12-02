package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

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
	private JTextField prefixField;
	private JLabel domainLabel;
	private JLabel portLabel;
	private JLabel pathLabel;
	private JLabel prefixLabel;
	private JScrollPane scroll;
	private JButton btn;

	public ProxyPanel(final SettingsChangedListener listener,
			ProxyTableModel proxyModel) {
		layout = new SpringLayout();
		setLayout(layout);
		proxyTable = new JTable(proxyModel);
		proxyTable.setColumnModel(new ProxyColumnModel());
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

		prefixLabel = new JLabel("Prefix");
		prefixField = SwingUtils.newJTextField();
		add(prefixLabel);
		add(prefixField);

		btn = new JButton("Ok");
		btn.setMargin(new Insets(0, 0, 0, 0));
		add(btn);

		initLayout();
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
				prefixLabel, null };
		Component[] fields = new Component[] { domainField, portField,
				pathField, prefixField, btn };
		int[] cols = new int[] { 250, 70, 250, 150, 60 };

		for (int i = cols.length - 1; i >= 0; i--) {
			JLabel lbl = labels[i];
			Component fld = fields[i];
			if (i == 0) {
				layout.putConstraint(SpringLayout.WEST, fld, 10,
						SpringLayout.WEST, this);
				if (lbl != null)
					layout.putConstraint(SpringLayout.WEST, lbl, 10,
							SpringLayout.WEST, this);
			} else {
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

			layout.putConstraint(SpringLayout.SOUTH, fld, -10,
					SpringLayout.SOUTH, this);

			if (lbl != null)
				layout.putConstraint(SpringLayout.SOUTH, lbl, -10,
						SpringLayout.NORTH, fld);

		}
		layout.putConstraint(SpringLayout.NORTH, btn, 0, SpringLayout.NORTH,
				domainField);


	}
}
