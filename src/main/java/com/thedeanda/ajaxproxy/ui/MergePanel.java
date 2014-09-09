package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class MergePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public MergePanel(final SettingsChangedListener listener,
			MergeTableModel mergeModel) {
		setLayout(new BorderLayout());
		JTable mergeTable = new JTable(mergeModel);
		mergeTable.setColumnModel(new MergeColumnModel());
		JScrollPane scroll = new JScrollPane(mergeTable);
		add(BorderLayout.CENTER, scroll);
		mergeModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				listener.settingsChanged();
				listener.restartRequired();
			}
		});
	}
}
