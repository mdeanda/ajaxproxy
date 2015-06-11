package com.thedeanda.ajaxproxy.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ResourceListModel implements ListModel<Resource> {
	private static final long serialVersionUID = -1203515236578998042L;

	private List<ListDataListener> listeners = new ArrayList<>();
	private List<Resource> items = new ArrayList<>();

	public void add(Resource item) {
		items.add(item);

		for (ListDataListener listener : listeners) {
			listener.intervalAdded(new ListDataEvent(this,
					ListDataEvent.INTERVAL_ADDED, items.size() - 1, items
							.size()));
		}
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public void clear() {
		int size = items.size();
		items.clear();

		if (size > 0) {
			for (ListDataListener listener : listeners) {
				listener.intervalRemoved(new ListDataEvent(this,
						ListDataEvent.INTERVAL_REMOVED, 0, size - 1));
			}
		}

	}

	public Resource get(int index) {
		if (index > items.size())
			return null;
		else
			return items.get(index);
	}

	public void notifyUpdated(UUID id) {
		int index = -1;
		for (int i = 0; i < items.size(); i++) {
			Resource r = items.get(i);
			if (r.getId() == id) {
				index = i;
				break;
			}
		}

		if (index >= 0) {
			for (ListDataListener listener : listeners) {
				listener.contentsChanged(new ListDataEvent(this,
						ListDataEvent.CONTENTS_CHANGED, index, index));
			}
		}
	}

	public Resource get(UUID id) {
		// TODO: use a map
		for (Resource r : items) {
			if (r.getId() == id) {
				return r;
			}
		}
		return null;
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public Resource getElementAt(int index) {
		if (index > items.size())
			return null;
		else
			return items.get(index);
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public void remove(int index) {
		if (index > items.size())
			return;

		items.remove(index);

		for (ListDataListener listener : listeners) {
			listener.intervalRemoved(new ListDataEvent(this,
					ListDataEvent.INTERVAL_REMOVED, index, index));
		}
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
}
