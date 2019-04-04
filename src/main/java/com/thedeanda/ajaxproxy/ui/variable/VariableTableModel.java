package com.thedeanda.ajaxproxy.ui.variable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.variable.controller.VariableController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.variable.model.Variable;
import com.thedeanda.javajson.JsonObject;

public class VariableTableModel extends AbstractTableModel implements SettingsChangedListener {
	private static final Logger log = LoggerFactory.getLogger(VariableTableModel.class);
	private static final long serialVersionUID = 1L;
	private final static String[] COLS = { "key", "value" };
	private final VariableController variableController;

	public VariableTableModel(VariableController variableController) {
		this.variableController = variableController;
		variableController.addListener(this);
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}



	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public int getColumnCount() {
		return COLS.length;
	}

	@Override
	public int getRowCount() {
		return variableController.getSize();
	}

	@Override
	public Object getValueAt(int row, int col) {
		String value = variableController.get(row).map(var -> {
				if (col == 0)
					return var.getKey();
				else
					return var.getValue();
			}).orElse(null);
		log.debug("get: {}/{} -> {}", row, col, value);
		return value;
		// return data.getJsonObject(row).getString(COLS[col]);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		return;
	}

	public void updateValue(String oldKey, String newKey, String newValue) {
		if (oldKey != null && !oldKey.equals(newKey)) {
			variableController.remove(oldKey);
			// TODO: fire row removed
		}
		if (newKey != null && !newKey.trim().equals(""))
			variableController.set(newKey, newValue);
		
		// TODO: fire row added instead of all data changed...
		fireTableDataChanged();
	}

	@Override
	public void settingsChanged() {
		fireTableDataChanged();
	}

	@Override
	public void restartRequired() {

	}
}
