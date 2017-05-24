package com.thedeanda.ajaxproxy.ui.logger;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessage;
import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessageListener;
import com.thedeanda.javajson.JsonArray;

public class LoggerTableModel extends AbstractTableModel implements LoggerMessageListener {
	private static final long serialVersionUID = 4961880986671181480L;

	private List<LoggerMessage> items = new ArrayList<>();

	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return 5;
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
			return message.getIndex();
		case 2:
			return message.getTs();
		case 3:
			return message.getTime();
		case 4:
			return message.getTag();
		case 5:
			return getMessage(message);
		default:
			return null;
		}
	}

	private String getMessage(LoggerMessage msg) {
		String output = "";

		JsonArray arr = msg.getMessage();
		// TODO: json library needs isEmpty on array
		if (arr != null && arr.size() > 0) {
			if (arr.size() == 1) {
				// TODO: first item may not be a string, json library needs a
				// get jsonvalue method
				output = arr.getString(0);
			} else {
				output = arr.toString();
			}
		}

		return output;
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
