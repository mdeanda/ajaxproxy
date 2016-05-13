package com.thedeanda.ajaxproxy.ui.variable;

import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.model.config.Variable;
import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.javajson.JsonObject;

public class VariablesPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(VariablesPanel.class);
	private static final long serialVersionUID = 1L;
	private VariableEditor variableEditor;
	private VariableTableModel variableModel;
	private SettingsChangedListener listener;
	private JTable variableTable;

	public VariablesPanel(final SettingsChangedListener listener) {
		variableModel = new VariableTableModel();

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		variableTable = new JTable(variableModel);
		variableTable.setColumnModel(new VariableColumnModel());
		JScrollPane scroll = new JScrollPane(variableTable);
		add(scroll);

		variableEditor = new VariableEditor(this);
		add(variableEditor);

		this.listener = listener;

		final ListSelectionModel cellSelectionModel = variableTable.getSelectionModel();
		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					startEdit();
				}
			}

		});

		layout.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.NORTH, variableEditor);

		layout.putConstraint(SpringLayout.WEST, variableEditor, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, variableEditor, -10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, variableEditor, -10, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, variableEditor, -60, SpringLayout.SOUTH, this);
	}

	private void startEdit() {
		int row = variableTable.getSelectedRow();
		log.trace("start edit {}", row);
		Variable value = variableModel.getValue(row);
		if (value == null) {
			value = new Variable();
		}
		variableEditor.startEdit(value);
	}

	public void changeValue(String oldKey, String newKey, String newValue) {
		variableModel.updateValue(oldKey, newKey, newValue);
		listener.settingsChanged();
		listener.restartRequired();
	}

	public JsonObject getConfig() {
		return variableModel.getConfig();
	}

	public void clear() {
		variableModel.clear();
	}

	public void setConfig(JsonObject jsonObject) {
		variableModel.setConfig(jsonObject);
	}

	public void setVariables(Map<String, String> vars) {
		variableModel.setValues(vars);
	}

}
