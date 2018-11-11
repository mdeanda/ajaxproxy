package com.thedeanda.ajaxproxy.ui.rest;

import javax.swing.*;
import java.util.List;

public class HistoryListModel extends AbstractListModel<HistoryItem> {
	private static final long serialVersionUID = 4328488269405279215L;
	private List<HistoryItem> items;

	public HistoryListModel() {
		reload();
	}

	public void reload() {
		HistoryItemService service = HistoryItemService.get();
		items = service.list();

		this.fireContentsChanged(this, 0, items.size());
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public HistoryItem getElementAt(int index) {
		return items.get(index);
	}

}
