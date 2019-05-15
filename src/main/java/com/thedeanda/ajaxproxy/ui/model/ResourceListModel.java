package com.thedeanda.ajaxproxy.ui.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;

import org.apache.http.Header;

import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.ui.resourceviewer.filter.RequestFilter;
import com.thedeanda.ajaxproxy.ui.resourceviewer.filter.RequestType;

public class ResourceListModel extends AbstractListModel<Resource> {
	private static final long serialVersionUID = -8347782415802894185L;
	private Set<Resource> unfilteredItems = new TreeSet<>();
	private List<Resource> items = new ArrayList<>();
	private Map<String, Resource> resourceMap = new HashMap<>();

	private ResourceService resourceService;
	private RequestFilter requestFilter;

	public ResourceListModel(ResourceService resourceService) {
		this.resourceService = resourceService;
		requestFilter = new RequestFilter();
	}

	public void add(Resource item) {
		synchronized (unfilteredItems) {
			if (item.getId() != null) {
				resourceMap.put(item.getId().toString(), item);
			}
			unfilteredItems.add(item);
		}
		if (requestFilter.accept(item)) {
			addSorted(item, items);
		}
	}

	private void addSorted(Resource res, List<Resource> list) {
		int index = 0;
		for (index = list.size() - 1; index >= 0; index--) {
			Resource item = list.get(index);
			if (item != null && item.getStartTime() > res.getStartTime()) {
				break;
			}
		}

		if (index >= 0) {
			list.add(index, res);
		} else {
			index = list.size();
			list.add(res);
		}
		fireIntervalAdded(this, index, index);
	}

	private void resetFilter() {
		int size = items.size();
		items.clear();
		if (size > 0) {
			fireIntervalRemoved(this, 0, size - 1);
		}

		Set<Resource> copyOfAllItems = null;
		synchronized (unfilteredItems) {
			copyOfAllItems = new TreeSet<>(unfilteredItems);
		}

		for (Resource item : copyOfAllItems) {
			if (requestFilter.accept(item)) {
				items.add(item);
			}
		}

		size = items.size();
		if (size > 0) {
			fireIntervalAdded(this, 0, size - 1);
		}

	}

	public void clear() {
		int size = items.size();
		items.clear();
		synchronized (unfilteredItems) {
			resourceMap.clear();
			unfilteredItems.clear();
		}

		if (size > 0) {
			fireIntervalRemoved(this, 0, size - 1);
		}

	}

	public Resource get(int index) {
		if (index > items.size())
			return null;
		else
			return items.get(index);
	}

	private void notifyUpdated(final UUID id) {
		int index = -1;
		for (int i = 0; i < items.size(); i++) {
			Resource r = items.get(i);
			if (r != null && r.getId() == id) {
				index = i;
				break;
			}
		}

		if (index >= 0) {
			final int theIndex = index;
			fireContentsChanged(this, theIndex, theIndex);
		}
	}

	public Resource get(UUID id) {
		if (id == null) {
			throw new NullPointerException("uuid is null?");
		}
		synchronized (unfilteredItems) {
			return resourceMap.get(id.toString());
		}
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

		fireIntervalRemoved(this, index, index);
	}

	public void startRequest(UUID id, URL url, Header[] requestHeaders, byte[] data) {

		Resource resource = get(id);
		if (resource != null) {
			resource.setUrlObject(url);
			resource.setRequestHeaders(requestHeaders);

			notifyUpdated(id);
		}
	}

	public void requestComplete(UUID id, int status, String reason, long duration, Header[] responseHeaders,
			byte[] data) {
		Resource resource = get(id);
		if (resource != null) {
			resource.setStatus(status);
			resource.setReason(reason);
			resource.setDuration(duration);
			resource.setResponseHeaders(responseHeaders);
			notifyUpdated(id);

			if (!requestFilter.accept(resource) && items.contains(resource)) {
				int index = items.indexOf(resource);
				items.remove(resource);
				fireIntervalRemoved(this, index, index);
			}
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

	public void setFilter(Pattern filterOutRegEx, Pattern filterRegEx, List<RequestType> checkedItems) {
		requestFilter.setFilterOutRegEx(filterOutRegEx);
		requestFilter.setRegEx(filterRegEx);
		requestFilter.setRequestTypes(checkedItems);
		resetFilter();
	}

}
