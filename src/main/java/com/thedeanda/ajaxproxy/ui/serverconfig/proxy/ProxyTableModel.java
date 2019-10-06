package com.thedeanda.ajaxproxy.ui.serverconfig.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.model.config.Convertor;
import com.thedeanda.ajaxproxy.ui.util.Reorderable;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonValue;

public class ProxyTableModel extends AbstractTableModel implements Reorderable {
	private static final Logger log = LoggerFactory.getLogger(ProxyTableModel.class);
	private static final long serialVersionUID = 1L;
	private List<ProxyConfig> data;
	private final static String PROTOCOL = "protocol";
	private final static String DOMAIN = "domain";
	private final static String PORT = "port";
	private final static String PATH = "path";
	// private final static String NEW_PROXY = "newProxy";
	private final static String[] COLS = { PROTOCOL, DOMAIN, PORT, PATH };

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

	public JsonArray getConfig(final int cacheTime) {
		// normalizeData();
		JsonArray arr = new JsonArray();
		Convertor converter = Convertor.get();
		for (ProxyConfig config : data) {
			if (config instanceof ProxyConfigRequest) {
				ProxyConfigRequest proxyConfigRequest = (ProxyConfigRequest) config;
				proxyConfigRequest.setCacheDuration(cacheTime);
				arr.add(converter.toJson(proxyConfigRequest));
			} else if (config instanceof ProxyConfigFile) {
				ProxyConfigFile proxyConfigRequest = (ProxyConfigFile) config;
				arr.add(converter.toJson(proxyConfigRequest));
			}
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
		if (config instanceof ProxyConfigRequest) {
			return getValueForRequest((ProxyConfigRequest) config, col);
		} else {
			return getValueForFile((ProxyConfigFile) config, col);
		}
	}

	public Object getValueForRequest(ProxyConfigRequest config, int col) {

		switch (col) {
		case 0:
			return config.getProtocol();
		case 1:
			return config.getHost().getOriginalValue();
		case 2:
			return config.getPort();
		case 3:
			return config.getPath().getOriginalValue();
		case 4:
			return config.isEnableCache();
		}
		return null;
	}

	public Object getValueForFile(ProxyConfigFile file, int col) {
		switch (col) {
		case 0:
			return "file";
		case 1:
			return file.getBasePath().getOriginalValue();
		case 3:
			return file.getPath().getOriginalValue();
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

	public int addValue(ProxyConfig updatedValue) {
		data.add(updatedValue);
		return data.size();
	}

	private void normalizeData() {
		List<ProxyConfig> toRemove = new ArrayList<>();
		for (ProxyConfig proxyConfig : data) {
			boolean keep = true;
			if (proxyConfig instanceof ProxyConfigRequest) {
				ProxyConfigRequest config = (ProxyConfigRequest) proxyConfig;
				if (config.getPort() <= 0)
					keep = false;
				if (StringUtils.isBlank(config.getHost().getOriginalValue()))
					keep = false;
				if (StringUtils.isBlank(config.getPath().getOriginalValue()))
					keep = false;
			} else {
				ProxyConfigFile fileConfig = (ProxyConfigFile) proxyConfig;
				if (StringUtils.isBlank(fileConfig.getPath().getOriginalValue()))
					keep = false;
				if (StringUtils.isBlank(fileConfig.getBasePath().getOriginalValue()))
					keep = false;
			}

			if (!keep) {
				toRemove.add(proxyConfig);
			}
		}
		if (!toRemove.isEmpty()) {
			for (ProxyConfig config : toRemove) {
				data.remove(config);
			}
			fireTableDataChanged();
		}
	}

	public void setConfig(ServerConfig serverConfig) {
		this.data.clear();
		this.data.addAll(serverConfig.getProxyConfig());

		this.fireTableDataChanged();
		this.normalizeData();
	}

	@Override
	public void reorder(int fromIndex, int toIndex) {
		if (fromIndex < 0 || fromIndex >= data.size() || toIndex < 0)
			return;
		if (toIndex > data.size()) {
			toIndex = data.size();
		}

		ProxyConfig item = data.remove(fromIndex);
		if (fromIndex < toIndex) {
			toIndex--;
		}
		data.add(toIndex, item);
	}
}
