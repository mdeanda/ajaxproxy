package com.thedeanda.ajaxproxy.ui.logger;

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void messageReceived(LoggerMessage message) {
		// TODO: swing worker/thread
		this.items.add(messages);
	}
}
