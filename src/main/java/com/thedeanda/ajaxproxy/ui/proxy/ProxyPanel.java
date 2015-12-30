package com.thedeanda.ajaxproxy.ui.proxy;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.thedeanda.ajaxproxy.model.config.ProxyConfig;
import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;

public class ProxyPanel extends JPanel implements EditorListener {
	private static final long serialVersionUID = 1L;
	private JTable proxyTable;
	private SpringLayout layout;
	private JScrollPane scroll;
	private ProxyTableModel proxyModel;
	private ProxyEditorPanel editor;

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

		editor = new ProxyEditorPanel(this);
		add(editor);
		initLayout();
	}

	private void startEdit() {
		int row = proxyTable.getSelectedRow();
		ProxyConfig config = proxyModel.getProxyConfig(row);
		editor.startEdit(config);
	}

	@Override
	public void commitChanges(ProxyConfig config) {
		int row = proxyTable.getSelectedRow();
		if (row < 0) {
			row = proxyModel.getRowCount() - 1;
		}
		proxyModel.setValue(row, config);
		proxyTable.changeSelection(row, 0, false, true);

	}

	private void initLayout() {
		layout.putConstraint(SpringLayout.SOUTH, editor, -10,
				SpringLayout.SOUTH, this);

		// table
		layout.putConstraint(SpringLayout.NORTH, scroll, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -5,
				SpringLayout.NORTH, editor);

	}
}
