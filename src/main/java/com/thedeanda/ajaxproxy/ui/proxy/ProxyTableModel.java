package com.thedeanda.ajaxproxy.ui.proxy;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class ProxyTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private JsonArray data;
	private final static String DOMAIN = "domain";
	private final static String PORT = "port";
	private final static String PATH = "path";
	private final static String NEW_PROXY = "newProxy";
	private final static String[] COLS = { DOMAIN, PORT, PATH, NEW_PROXY };
	private final static FieldType[] TYPES = { FieldType.String,
			FieldType.Number, FieldType.String, FieldType.Boolean };
	private final static int[] COLS_TO_CHECK_FOR_NON_EMPTY = new int[]{0, 2};

	private enum FieldType {
		String, Boolean, Number
	};

	public ProxyTableModel(JsonArray data) {
		this.data = data;
	}

	public ProxyTableModel() {
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

	public JsonArray getConfig() {
		normalizeData();
		JsonArray arr = new JsonArray();
		for (JsonValue v : data) {
			arr.add(v.getJsonObject());
		}
		arr.remove(arr.size() - 1);
		return arr;
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
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (data.size() < row || row < 0)
			return null;
		JsonObject json = data.getJsonObject(row);
		if (json == null)
			return null;

		switch (TYPES[col]) {
		case Boolean:
			return json.getBoolean(COLS[col]);
		case Number:
			return json.getInt(COLS[col]);
		case String:
			return json.getString(COLS[col]);
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (value == null)
			value = "";
		data.getJsonObject(rowIndex).put(COLS[columnIndex], value.toString());
		normalizeData();
	}

	public void setValue(int row, String domain, int port, String path,
			Object newProxy) {
		JsonObject json = data.getJsonObject(row);
		if (json != null) {
			json.put(DOMAIN, domain);
			json.put(PORT, port);
			json.put(PATH, path);
			json.put(NEW_PROXY, newProxy);
			fireTableRowsUpdated(row, row);
			normalizeData();
		}
	}

	private void normalizeData() {
		boolean changed = false;
		for (int j = 0; j < data.size(); j++) {
			JsonObject rowObj = data.getJsonObject(j);
			boolean keep = false;
			for (int i = 0; i < COLS_TO_CHECK_FOR_NON_EMPTY.length; i++) {
				String v = rowObj.getString(COLS[COLS_TO_CHECK_FOR_NON_EMPTY[i]]);
				if (!StringUtils.isBlank(v)) {
					keep = true;
					break;
				}
			}
			if (!keep) {
				data.remove(j);
				j--;
				changed = true;
			}
		}
		data.add(new JsonObject());
		fireTableDataChanged();
	}

	public void setConfig(JsonArray data) {
		if (data == null)
			data = new JsonArray();
		this.data = data;
		this.fireTableDataChanged();
		this.normalizeData();
	}
}
