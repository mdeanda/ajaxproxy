package com.thedeanda.ajaxproxy.ui;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.javajson.JsonArray;
import net.sourceforge.javajson.JsonObject;
import net.sourceforge.javajson.JsonValue;

public class VariableTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private JsonArray data;
	private final static String[] COLS = { "key", "value" };

	public VariableTableModel(JsonArray data) {
		this.data = data;
	}

	public VariableTableModel() {
		this.data = new JsonArray();
		data.add(new JsonObject());
		data.add(new JsonObject());
		fireTableDataChanged();
	}

	public void clear() {
		data = new JsonArray();
		fireTableDataChanged();
		normalizeData();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}

	public JsonObject getData() {
		JsonObject ret = new JsonObject();
		normalizeData();
		JsonArray arr = new JsonArray();
		for (JsonValue v : data) {
			arr.add(v.getJsonObject());
		}
		arr.remove(arr.size() - 1);

		for (JsonValue v : arr) {
			JsonObject tmp = v.getJsonObject();
			String key = tmp.getString(COLS[0]);
			String value = tmp.getString(COLS[1]);
			if (key != null && value != null)
				ret.put(key, value);
		}
		return ret;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public int getColumnCount() {
		return COLS.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data.getJsonObject(row).getString(COLS[col]);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (value == null)
			value = "";
		data.getJsonObject(rowIndex).put(COLS[columnIndex], value.toString());
		normalizeData();
	}

	private void normalizeData() {
		for (int j = 0; j < data.size(); j++) {
			JsonObject rowObj = data.getJsonObject(j);
			boolean keep = false;
			for (int i = 0; i < COLS.length; i++) {
				String v = rowObj.getString(COLS[i]);
				if (v != null && !v.equals("")) {
					keep = true;
					break;
				}
			}
			if (!keep) {
				data.remove(j);
				fireTableRowsDeleted(j, j);
				normalizeData();
				return;
			}
		}
		data.add(new JsonObject());
		fireTableRowsInserted(data.size(), data.size());
	}

	public void setData(JsonObject jsonObject) {
		if (jsonObject == null)
			jsonObject = new JsonObject();
		this.data = new JsonArray();
		for (String key : jsonObject) {
			JsonObject tmp = new JsonObject();
			tmp.put(COLS[0], key);
			tmp.put(COLS[1], jsonObject.getString(key));
			data.add(tmp);
		}
		this.fireTableDataChanged();
		this.normalizeData();
	}
}
