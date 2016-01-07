package com.thedeanda.ajaxproxy.ui.variable;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class VariableTableModel extends AbstractTableModel {
	private static final Logger log = LoggerFactory
			.getLogger(VariableTableModel.class);
	private static final long serialVersionUID = 1L;
	private JsonArray data;
	private final static String[] COLS = { "key", "value" };

	public VariableTableModel(JsonArray data) {
		this.data = data;
	}

	public void set(String key, String value) {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
			log.warn("missing key/value {}/{}", key, value);
			return;
		}
		log.warn("{}", data);
		for (JsonValue val : data) {
			JsonObject json = val.getJsonObject();
			if (key.equals(json.getString(COLS[0]))) {
				json.put(COLS[1], value);
				return;
			}
		}
		JsonObject json = new JsonObject();
		json.put(COLS[0], key);
		json.put(COLS[1], value);
		data.add(json);
		//fireTableRowsInserted(data.size() - 1, data.size());
		normalizeData();
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

	public JsonObject getConfig() {
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

	public void setConfig(JsonObject jsonObject) {
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
