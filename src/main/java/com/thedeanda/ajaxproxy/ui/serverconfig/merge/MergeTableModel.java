package com.thedeanda.ajaxproxy.ui.serverconfig.merge;

import javax.swing.table.AbstractTableModel;

import com.thedeanda.ajaxproxy.config.model.MergeMode;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class MergeTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private JsonArray data;
	private final static String[] COLS = { "path", "minify", "filePath", "mode" };

	public MergeTableModel(JsonArray data) {
		this.data = data;
	}

	public MergeTableModel() {
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
		switch (col) {
		case 1:
			return Boolean.class;
		case 3:
			return MergeMode.class;
		default:
			return String.class;
		}
	}

	public JsonArray getConfig() {
		//normalizeData();
		JsonArray arr = new JsonArray();
		for (JsonValue v : data) {
			arr.add(v.getJsonObject());
		}
		arr.remove(arr.size() - 1);
		return arr;
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
		if (col == 3) {
			String m = data.getJsonObject(row).getString(COLS[col]);
			if (m == null)
				return null;
			try {
				return MergeMode.valueOf(m);
			} catch (IllegalArgumentException e) {
				return null;
			}
		} else if (col != 1)
			return data.getJsonObject(row).getString(COLS[col]);
		else
			return data.getJsonObject(row).getBoolean(COLS[col]);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (value == null)
			value = "";
		switch (columnIndex) {
		case 1:
			data.getJsonObject(rowIndex).put(COLS[columnIndex],
					Boolean.valueOf(value.toString()));
			break;
		default:
			data.getJsonObject(rowIndex).put(COLS[columnIndex],
					value.toString());
		}
		normalizeData();
	}

	private void normalizeData() {
		for (int j = 0; j < data.size(); j++) {
			JsonObject rowObj = data.getJsonObject(j);
			boolean keep = false;
			for (int i = 0; i < COLS.length; i++) {
				if (i != 1) { // ignore boolean column
					String v = rowObj.getString(COLS[i]);
					if (v != null && !v.equals("")) {
						keep = true;
						break;
					}
				} else if (i == 1) {
					// maybe not fully ignore b/c it acts weird when checked
					// before other fields set
					if (rowObj.getBoolean(COLS[i])) {
						keep = true;
						break;
					}
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

	public void setConfig(JsonArray data, ServerConfig serverConfig) {
		//TODO: revisit to work with server config data instead
		if (data == null)
			data = new JsonArray();
		this.data = data;
		this.fireTableDataChanged();
		this.normalizeData();
	}
}
