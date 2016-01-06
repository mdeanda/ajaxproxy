package com.thedeanda.ajaxproxy.ui.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.model.config.Convertor;
import com.thedeanda.ajaxproxy.model.config.ProxyConfig;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonValue;

public class ProxyTableModel extends AbstractTableModel {
	private static final Logger log = LoggerFactory
			.getLogger(ProxyTableModel.class);
	private static final long serialVersionUID = 1L;
	private List<ProxyConfig> data;
	private final static String DOMAIN = "domain";
	private final static String PORT = "port";
	private final static String PATH = "path";
	private final static String NEW_PROXY = "newProxy";
	private final static String[] COLS = { DOMAIN, PORT, PATH, NEW_PROXY };

	public ProxyTableModel() {
		log.debug("new table model");
		this.data = new ArrayList<>();
		fireTableDataChanged();
	}

	public void clear() {
		data.clear();
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
		Convertor converter = Convertor.get();
		for (ProxyConfig config : data) {
			arr.add(converter.toJson(config));
		}
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
		int count = data.size() + 1;
		count = Math.max(2, count);
		return count;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (data.size() <= row || row < 0)
			return null;
		ProxyConfig config = data.get(row);

		switch (col) {
		case 0:
			return config.getHost();
		case 1:
			return config.getPort();
		case 2:
			return config.getPath();
		case 3:
			return config.isNewProxy();
		}
		return null;
	}

	public ProxyConfig getProxyConfig(int row) {
		ProxyConfig config = null;
		if (data.size() > row) {
			config = data.get(row);
		}
		return config;
	}

	public void setValue(int row, ProxyConfig config) {
		if (data.size() > row) {
			data.remove(row);
			data.add(row, config);
			fireTableRowsUpdated(row, row);
		} else {
			data.add(config);
			fireTableRowsInserted(data.size() - 1, data.size() - 1);
		}
		normalizeData();
	}

	private void normalizeData() {
		List<ProxyConfig> toRemove = new ArrayList<>();
		for (ProxyConfig config : data) {
			boolean keep = true;
			if (config.getPort() <= 0)
				keep = false;
			if (StringUtils.isBlank(config.getHost()))
				keep = false;
			if (StringUtils.isBlank(config.getPath()))
				keep = false;
			if (!keep) {
				toRemove.add(config);
			}
		}
		if (!toRemove.isEmpty()) {
			for (ProxyConfig config : toRemove) {
				data.remove(config);
			}
			fireTableDataChanged();
		}
	}

	public void setConfig(JsonArray data) {
		if (data == null)
			data = new JsonArray();

		this.data.clear();
		Convertor converter = Convertor.get();
		for (JsonValue v : data) {
			ProxyConfig config = converter.readProxyConfig(v.getJsonObject());
			this.data.add(config);
		}
		this.fireTableDataChanged();
		this.normalizeData();
	}
}
