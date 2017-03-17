package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.thedeanda.ajaxproxy.model.config.ProxyConfig;
import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;

/**
 * top level proxy panel with list of configured proxy settings
 * 
 * @author mdeanda
 *
 */
public class ProxyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable proxyTable;
	private SpringLayout layout;
	private JScrollPane scroll;
	private ProxyTableModel proxyModel;
	private JPanel editor;

	public ProxyPanel(final SettingsChangedListener listener, final ProxyTableModel proxyModel) {
		layout = new SpringLayout();
		setLayout(layout);
		this.proxyModel = proxyModel;
		proxyTable = new JTable(proxyModel);
		proxyTable.setColumnModel(new ProxyColumnModel());
		proxyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		proxyTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_ENTER != e.getKeyCode()) {
					return;
				}
				startEdit();
			}
		});
		proxyTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
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

		// TODO: make this a toolbar perhaps to add "add new proxy" button
		editor = new JPanel();
		add(editor);
		initLayout();
	}

	private void startEdit() {
		int row = proxyTable.getSelectedRow();
		if (row < 0) {
			row = proxyModel.getRowCount() - 1;
		}
		ProxyConfig config = proxyModel.getProxyConfig(row);
		ProxyConfig updatedValue = ProxyEditorDialog.showEditDialog(config, scroll);
		if (updatedValue != null) {
			proxyModel.setValue(row, updatedValue);
			proxyTable.changeSelection(row, 0, false, true);
		}
	}

	private void initLayout() {
		layout.putConstraint(SpringLayout.SOUTH, editor, -10, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.EAST, editor, -10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, editor, 10, SpringLayout.WEST, this);

		// table
		layout.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.NORTH, editor);

	}
}
