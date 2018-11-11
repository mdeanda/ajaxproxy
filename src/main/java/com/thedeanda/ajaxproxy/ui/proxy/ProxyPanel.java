package com.thedeanda.ajaxproxy.ui.proxy;

import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
import com.thedeanda.ajaxproxy.ui.util.TableRowTransferHandler;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.*;

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
	private JPanel topPanel;
	private JPanel bottomPanel;
	private ProxyTableModel proxyModel;
	private JButton addProxyButton;
	private JButton addFileButton;
	private JButton editProxyButton;

	public ProxyPanel(final SettingsChangedListener listener, final ProxyTableModel proxyModel) {
		layout = new SpringLayout();
		setLayout(layout);
		this.proxyModel = proxyModel;

		topPanel = initTopPanel();
		add(topPanel);

		bottomPanel = initBottomPanel();
		add(bottomPanel);

		proxyModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				listener.settingsChanged();
				listener.restartRequired();
			}
		});

		initLayout();
	}

	private JPanel initTopPanel() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setBorder(new BottomBorder());

		this.addProxyButton = new JButton("Add Proxy");
		panel.add(addProxyButton);
		addProxyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startAdd();
			}
		});

		this.addFileButton = new JButton("Add Path");
		panel.add(addFileButton);
		addFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startAddFile();
			}
		});

		this.editProxyButton = new JButton("Edit");
		panel.add(editProxyButton);
		editProxyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startEdit();
			}
		});

		layout.putConstraint(SpringLayout.NORTH, addProxyButton, 20, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, addProxyButton, 10, SpringLayout.WEST, panel);

		layout.putConstraint(SpringLayout.BASELINE, addFileButton, 0, SpringLayout.BASELINE, addProxyButton);
		layout.putConstraint(SpringLayout.WEST, addFileButton, 10, SpringLayout.EAST, addProxyButton);

		layout.putConstraint(SpringLayout.BASELINE, editProxyButton, 0, SpringLayout.BASELINE, addFileButton);
		layout.putConstraint(SpringLayout.WEST, editProxyButton, 10, SpringLayout.EAST, addFileButton);

		return panel;
	}

	private JPanel initBottomPanel() {

		proxyTable = new JTable(proxyModel);
		proxyTable.setDragEnabled(true);
		proxyTable.setDropMode(DropMode.INSERT_ROWS);
		proxyTable.setTransferHandler(new TableRowTransferHandler(proxyTable));
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
		proxyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting())
					return;
			}
		});
		scroll = new JScrollPane(proxyTable);

		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.add(scroll);
		layout.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, panel);

		return panel;
	}

	private void startEdit() {
		int row = proxyTable.getSelectedRow();
		if (row < 0) {
			row = proxyModel.getRowCount() - 1;
		}
		ProxyConfig config = proxyModel.getProxyConfig(row);
		ProxyConfig updatedValue = null;
		if (config instanceof ProxyConfigRequest) {
			updatedValue = ProxyEditorDialog.showEditDialog((ProxyConfigRequest) config, scroll);
		} else if (config instanceof ProxyConfigFile) {
			updatedValue = ProxyEditorDialog.showEditDialog((ProxyConfigFile) config, scroll);
		}

		if (updatedValue != null) {
			proxyModel.setValue(row, updatedValue);
			proxyTable.changeSelection(row, 0, false, true);
		}
	}

	private void startAdd() {
		ProxyConfig updatedValue = ProxyEditorDialog.showAddProxyDialog(scroll);
		if (updatedValue != null) {
			int row = proxyModel.addValue(updatedValue);
			proxyTable.changeSelection(row - 1, 0, false, true);
		}
	}

	private void startAddFile() {
		ProxyConfig updatedValue = ProxyEditorDialog.showAddFileDialog(scroll);
		if (updatedValue != null) {
			int row = proxyModel.addValue(updatedValue);
			proxyTable.changeSelection(row - 1, 0, false, true);
		}
	}

	private void initLayout() {
		/*
		 * layout.putConstraint(SpringLayout.SOUTH, editor, -10, SpringLayout.SOUTH,
		 * this); layout.putConstraint(SpringLayout.EAST, editor, -10,
		 * SpringLayout.EAST, this); layout.putConstraint(SpringLayout.WEST, editor, 10,
		 * SpringLayout.WEST, this);
		 * 
		 * // table layout.putConstraint(SpringLayout.NORTH, scroll, 10,
		 * SpringLayout.NORTH, this); layout.putConstraint(SpringLayout.EAST, scroll,
		 * -10, SpringLayout.EAST, this); layout.putConstraint(SpringLayout.WEST,
		 * scroll, 10, SpringLayout.WEST, this);
		 * layout.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.NORTH,
		 * editor); //
		 */

		layout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, topPanel, 60, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, bottomPanel, 0, SpringLayout.SOUTH, topPanel);
		layout.putConstraint(SpringLayout.SOUTH, bottomPanel, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, bottomPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, bottomPanel, 0, SpringLayout.EAST, this);
	}
}
