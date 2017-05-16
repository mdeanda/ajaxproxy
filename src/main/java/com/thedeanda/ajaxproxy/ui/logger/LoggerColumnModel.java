package com.thedeanda.ajaxproxy.ui.logger;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class LoggerColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = -4286144913875203965L;
	private static final int COL_WIDTH_UID = 100;
	private static final int COL_WIDTH_TS = 100;
	private static final int COL_WIDTH_TIME = 50;
	private static final int COL_WIDTH_TAG = 100;

	private static final String COL_NAME_UID = "Uid";
	private static final String COL_NAME_TS = "TS";
	private static final String COL_NAME_TIME = "Time";
	private static final String COL_NAME_TAG = "Tag";

	public LoggerColumnModel() {

		TableColumn tc;
		tc = new TableColumn(0, COL_WIDTH_UID);
		tc.setHeaderValue(COL_NAME_UID);
		addColumn(tc);

		tc = new TableColumn(0, COL_WIDTH_TS);
		tc.setHeaderValue(COL_NAME_TS);
		addColumn(tc);

		tc = new TableColumn(0, COL_WIDTH_TIME);
		tc.setHeaderValue(COL_NAME_TIME);
		addColumn(tc);

		tc = new TableColumn(0, COL_WIDTH_TAG);
		tc.setHeaderValue(COL_NAME_TAG);
		addColumn(tc);
	}
}
