package com.thedeanda.ajaxproxy.ui.tracker;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class FileTrackerColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = 1L;

	public FileTrackerColumnModel() {
		TableColumn col = new TableColumn(0, 600);
		col.setHeaderValue("Path");
		this.addColumn(col);

		col = new TableColumn(1, 50);
		col.setHeaderValue("Count");
		this.addColumn(col);
	}
}
