package com.thedeanda.ajaxproxy.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class FileTrackerTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private List<String> files;
	private Map<String, Integer> count = new HashMap<String, Integer>();

	public FileTrackerTableModel() {
		files = new ArrayList<String>();
	}

	public void clear() {
		int rows;
		synchronized (files) {
			rows = files.size();
			files.clear();
		}
		synchronized (count) {
			count.clear();
		}
		fireTableRowsDeleted(0, rows);
	}

	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
		case 1:
			return Integer.class;
		default:
			return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 1)
			return count.get(files.get(row));
		else
			return files.get(row);
	}

	public void trackFile(String url, long duration) {
		int indexAdded = -1;
		int indexMod = -1;
		synchronized (files) {
			if (!files.contains(url)) {
				files.add(url);
				Collections.sort(files);
				indexAdded = files.indexOf(url);
			} else {
				indexMod = files.indexOf(url);
			}
		}

		synchronized (count) {
			if (count.containsKey(url)) {
				count.put(url, count.get(url) + 1);
			} else {
				count.put(url, 1);
			}
		}
		if (indexAdded >= 0)
			fireTableRowsInserted(indexAdded, indexAdded);
		if (indexMod >= 0)
			fireTableCellUpdated(indexMod, 1);
	}
}
