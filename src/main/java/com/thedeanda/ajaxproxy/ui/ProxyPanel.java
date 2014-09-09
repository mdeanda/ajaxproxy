package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class ProxyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ProxyTableModel proxyModel;
	private JTable proxyTable;
	private SettingsChangedListener listener;

	public ProxyPanel(final SettingsChangedListener listener,
			ProxyTableModel proxyModel) {
		this.listener = listener;
		setLayout(new BorderLayout());
		proxyTable = new JTable(proxyModel);
		proxyTable.setColumnModel(new ProxyColumnModel());
		JScrollPane scroll = new JScrollPane(proxyTable);

		add(BorderLayout.CENTER, scroll);

		proxyModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				listener.settingsChanged();
				listener.restartRequired();
			}
		});
	}
}
