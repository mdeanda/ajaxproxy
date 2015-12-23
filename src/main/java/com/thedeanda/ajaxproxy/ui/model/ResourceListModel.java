package com.thedeanda.ajaxproxy.ui.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.http.Header;

public class ResourceListModel implements ListModel<Resource> {
	private static final long serialVersionUID = -1203515236578998042L;

	private List<ListDataListener> listeners = new ArrayList<>();
	private Set<Resource> unfilteredItems = new TreeSet<>();
	private List<Resource> items = new ArrayList<>();

	private Pattern filterRegEx;

	public void add(Resource item) {
		synchronized (unfilteredItems) {
			unfilteredItems.add(item);
		}
		if (filterRegEx == null
				|| filterRegEx.matcher(item.getPath()).matches()) {
			addSorted(item, items);
		}

		for (ListDataListener listener : listeners) {
			listener.intervalAdded(new ListDataEvent(this,
					ListDataEvent.INTERVAL_ADDED, items.size() - 1, items
							.size()));
		}
	}

	private void addSorted(Resource res, List<Resource> list) {
		int index = 0;
		for (Resource item : list) {
			if (item.getStartTime() < res.getStartTime()) {
				index++;
			} else {
				break;
			}
		}

		if (index >= 0) {
			list.add(index, res);
		} else {
			list.add(res);
		}
	}

	private void resetFilter() {
		int size = items.size();
		items.clear();
		if (size > 0) {
			for (ListDataListener listener : listeners) {
				listener.intervalRemoved(new ListDataEvent(this,
						ListDataEvent.INTERVAL_REMOVED, 0, size - 1));
			}
		}

		Set<Resource> copyOfAllItems = null;
		synchronized (unfilteredItems) {
			copyOfAllItems = new TreeSet<>(unfilteredItems);
		}

		if (filterRegEx == null) {
			// just add all
			items.addAll(copyOfAllItems);
		} else {
			for (Resource item : copyOfAllItems) {
				if (filterRegEx.matcher(item.getPath()).matches()) {
					items.add(item);
				}
			}
		}

		size = items.size();
		if (size > 0) {
			for (ListDataListener listener : listeners) {
				listener.intervalAdded(new ListDataEvent(this,
						ListDataEvent.INTERVAL_ADDED, 0, size - 1));
			}
		}

	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public void clear() {
		int size = items.size();
		items.clear();
		synchronized (unfilteredItems) {
			unfilteredItems.clear();
		}

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

	private void notifyUpdated(UUID id) {
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
		if (id==null) {
			throw new NullPointerException("uuid is null?");
		}
		// TODO: use a map
		synchronized (unfilteredItems) {
			for (Resource r : unfilteredItems) {				
				if (r.getLoadedResource()!=null) {
					continue;
				}
				if (id.toString().equals(r.getId().toString())) {
					return r;
				}
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

		Resource resource = items.remove(index);
		if (resource != null) {
			unfilteredItems.remove(resource);
		}

		for (ListDataListener listener : listeners) {
			listener.intervalRemoved(new ListDataEvent(this,
					ListDataEvent.INTERVAL_REMOVED, index, index));
		}
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public void startRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {

		Resource resource = get(id);
		if (resource != null) {
			resource.setUrlObject(url);
			resource.setInputData(data);
			resource.setRequestHeaders(requestHeaders);

			notifyUpdated(id);
		}
	}

	public void requestComplete(UUID id, int status, String reason,
			long duration, Header[] responseHeaders, byte[] data) {
		Resource resource = get(id);
		if (resource != null) {
			resource.setStatus(status);
			resource.setReason(reason);
			resource.setDuration(duration);
			resource.setResponseHeaders(responseHeaders);
			resource.setOutputData(data);
			notifyUpdated(id);
		}
	}

	public void error(UUID id, String message, Exception e) {
		Resource resource = get(id);
		if (resource != null) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			resource.setErrorReason(message);
			resource.setException(sw.toString());
			notifyUpdated(id);
		}
	}

	public void setFilter(Pattern filterRegEx) {
		this.filterRegEx = filterRegEx;
		resetFilter();
	}
}
