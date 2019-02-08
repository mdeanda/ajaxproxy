package com.thedeanda.ajaxproxy.ui.serverconfig.merge;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;

public class MergePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public MergePanel(final SettingsChangedListener listener,
			MergeTableModel mergeModel) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JTable mergeTable = new JTable(mergeModel);
		mergeTable.setColumnModel(new MergeColumnModel());
		JScrollPane scroll = new JScrollPane(mergeTable);
		add(scroll);
		mergeModel.addTableModelListener(new TableModelListener() {
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
