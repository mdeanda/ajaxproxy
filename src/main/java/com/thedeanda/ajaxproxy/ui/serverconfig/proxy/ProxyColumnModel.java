package com.thedeanda.ajaxproxy.ui.serverconfig.proxy;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class ProxyColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = 1L;

	public ProxyColumnModel() {
		TableColumn col;
		col = new TableColumn(0, 50);
		col.setHeaderValue("Index");
		this.addColumn(col);

		col = new TableColumn(1, 50);
		col.setHeaderValue("Protocol");
		this.addColumn(col);

		col = new TableColumn(2, 300);
		col.setHeaderValue("Host");
		this.addColumn(col);

		col = new TableColumn(3, 50);
		col.setHeaderValue("Port");
		this.addColumn(col);

		col = new TableColumn(4, 400);
		col.setHeaderValue("Path");
		this.addColumn(col);

		col = new TableColumn(5, 80);
		col.setHeaderValue("Cached");
		this.addColumn(col);

		col = new TableColumn(6, 80);
		col.setHeaderValue("Cache TTL");
		this.addColumn(col);
	}
}
