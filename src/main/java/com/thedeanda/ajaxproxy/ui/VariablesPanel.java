package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class VariablesPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public VariablesPanel(final SettingsChangedListener listener,
			VariableTableModel variableModel) {
		setLayout(new BorderLayout());
		JTable variableTable = new JTable(variableModel);
		variableTable.setColumnModel(new VariableColumnModel());
		add(BorderLayout.CENTER, variableTable);
		variableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				listener.settingsChanged();
				listener.restartRequired();
			}
		});
	}

}
