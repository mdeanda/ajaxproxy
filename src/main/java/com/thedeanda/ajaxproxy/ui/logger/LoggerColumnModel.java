package com.thedeanda.ajaxproxy.ui.logger;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class LoggerColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = -4286144913875203965L;
	private static final int COL_WIDTH_UID = 100;
	private static final int COL_WIDTH_INDEX = 50;
	private static final int COL_WIDTH_TS = 50;
	private static final int COL_WIDTH_TIME = 70;
	private static final int COL_WIDTH_TAG = 150;
	private static final int COL_WIDTH_MSG = 200;

	private static final String COL_NAME_UID = "Uid";
	private static final String COL_NAME_INDEX = "Index";
	private static final String COL_NAME_TS = "TS";
	private static final String COL_NAME_TIME = "Time";
	private static final String COL_NAME_TAG = "Tag";
	private static final String COL_NAME_MSG = "Message";

	public LoggerColumnModel() {

		TableColumn tc;
		tc = new TableColumn(0, COL_WIDTH_UID);
		tc.setHeaderValue(COL_NAME_UID);
		addColumn(tc);

		tc = new TableColumn(1, COL_WIDTH_INDEX);
		tc.setHeaderValue(COL_NAME_INDEX);
		addColumn(tc);

		tc = new TableColumn(2, COL_WIDTH_TS);
		tc.setHeaderValue(COL_NAME_TS);
		addColumn(tc);

		tc = new TableColumn(3, COL_WIDTH_TIME);
		tc.setHeaderValue(COL_NAME_TIME);
		addColumn(tc);

		tc = new TableColumn(4, COL_WIDTH_TAG);
		tc.setHeaderValue(COL_NAME_TAG);
		addColumn(tc);

		tc = new TableColumn(5, COL_WIDTH_MSG);
		tc.setHeaderValue(COL_NAME_MSG);
		addColumn(tc);
	}

}
