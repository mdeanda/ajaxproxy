package com.thedeanda.ajaxproxy.ui.logger;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessage;
import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessageListener;

public class LoggerTableModel extends AbstractTableModel implements LoggerMessageListener {
	private static final long serialVersionUID = 4961880986671181480L;

	private List<LoggerMessage> items = new ArrayList<>();

	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (items.size() > rowIndex) {
			LoggerMessage message = items.get(rowIndex);
			return getValue(message, columnIndex);
		}
		return null;
	}

	private Object getValue(LoggerMessage message, int col) {
		switch (col) {
		case 0:
			return message.getUid();
		case 1:
			return message.getTs();
		case 2:
			return message.getTime();
		case 3:
			return message.getTag();
		default:
			return null;
		}
	}

	@Override
	public void messageReceived(LoggerMessage message) {
		// TODO: swing worker/thread ?
		int row = items.size();
		items.add(message);

		fireTableRowsInserted(row, row);
	}

	public LoggerMessage getMessage(int index) {
		if (items.size() > index && index >= 0) {
			return items.get(index);
		}
		return null;
	}
}
