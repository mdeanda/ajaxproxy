package com.thedeanda.ajaxproxy.ui.variable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;

public class VariablesPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public VariablesPanel(final SettingsChangedListener listener,
			VariableTableModel variableModel) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JTable variableTable = new JTable(variableModel);
		variableTable.setColumnModel(new VariableColumnModel());
		JScrollPane scroll = new JScrollPane(variableTable);
		add(scroll);
		variableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				listener.settingsChanged();
				listener.restartRequired();
			}
		});

		layout.putConstraint(SpringLayout.NORTH, scroll, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10,
				SpringLayout.SOUTH, this);

	}

}
