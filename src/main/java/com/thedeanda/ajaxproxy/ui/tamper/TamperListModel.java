package com.thedeanda.ajaxproxy.ui.tamper;

import com.thedeanda.ajaxproxy.model.tamper.TamperItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TamperListModel extends AbstractListModel<TamperItem> {
	private static final Logger log = LoggerFactory
			.getLogger(TamperListModel.class);
	private static final long serialVersionUID = -7159966628455887784L;

	private List<TamperItem> items = new ArrayList<>();

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public TamperItem getElementAt(int index) {
		return items.get(index);
	}

	public void add(TamperItem element) {
		int size = items.size();
		items.add(element);
		fireIntervalAdded(this, size, size);
	}

	public List<TamperItem> items() {
		return new ArrayList<>(items);
	}

	public void clear() {
		int oldSize = items.size();
		items.clear();
		fireIntervalRemoved(this, 0, oldSize);
	}

}
