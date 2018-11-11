package com.thedeanda.ajaxproxy.ui.variable;

import com.thedeanda.ajaxproxy.ui.variable.model.Variable;
import com.thedeanda.javajson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class VariableTableModel extends AbstractTableModel {
	private static final Logger log = LoggerFactory.getLogger(VariableTableModel.class);
	private static final long serialVersionUID = 1L;
	private final static String[] COLS = { "key", "value" };
	private Map<String, String> dataset = new TreeMap<>();

	public VariableTableModel() {
	}

	public void clear() {
		dataset.clear();
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}

	public JsonObject getConfig() {
		JsonObject ret = new JsonObject();

		for (String key : dataset.keySet()) {
			ret.put(key, dataset.get(key));
		}
		return ret;
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
		log.debug("rows: {}", dataset.size());
		return dataset.size() + 1;
	}

	@Override
	public Object getValueAt(int row, int col) {
		String value = null;
		ArrayList<String> keys = new ArrayList<>(dataset.keySet());
		if (row >= 0 && row < keys.size()) {
			if (col == 0)
				value = keys.get(row);
			else
				value = dataset.get(keys.get(row));
		}
		log.debug("get: {}/{} -> {}", row, col, value);
		return value;
		// return data.getJsonObject(row).getString(COLS[col]);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		return; // NOTE: might need to reimplement this
		/*
		 * if (value == null) value = "";
		 * data.getJsonObject(rowIndex).put(COLS[columnIndex],
		 * value.toString()); normalizeData();
		 */
	}

	public void setConfig(JsonObject jsonObject) {
		if (jsonObject == null)
			jsonObject = new JsonObject();
		dataset.clear();
		for (String key : jsonObject) {
			dataset.put(key, jsonObject.getString(key));
		}
		this.fireTableDataChanged();
	}

	public void setValues(Map<String, String> vars) {
		if (vars != null) {
			for (String key : vars.keySet()) {
				String value = vars.get(key);
				dataset.put(key, value);
			}
		}
		fireTableDataChanged();
	}

	public Variable getValue(int row) {
		Variable var = null;
		if (row >= 0 && row < dataset.size()) {
			var = new Variable();
			var.setKey((String) getValueAt(row, 0));
			var.setValue((String) getValueAt(row, 1));
		}
		return var;
	}

	public void updateValue(String oldKey, String newKey, String newValue) {
		if (oldKey != null && !oldKey.equals(newKey)) {
			dataset.remove(oldKey);
			// TODO: fire row removed
		}
		if (newKey != null && !newKey.trim().equals(""))
			dataset.put(newKey, newValue);
		
		// TODO: fire row added instead of all data changed...
		fireTableDataChanged();
	}
}
