package com.thedeanda.ajaxproxy.ui;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class MergeColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = 1L;

	public MergeColumnModel() {
		TableColumn col = new TableColumn(0, 300);
		col.setHeaderValue("Path");
		this.addColumn(col);

		col = new TableColumn(1, 50);
		col.setHeaderValue("Minify");
		this.addColumn(col);

		col = new TableColumn(2, 400);
		col.setHeaderValue("File Path");
		this.addColumn(col);

		col = new TableColumn(3, 100);
		col.setHeaderValue("Mode");
		col.setCellEditor(new MergeModeCellEditor());
		//col.setCellRenderer(new MergeModeCellEditor());
		this.addColumn(col);
	}
}
