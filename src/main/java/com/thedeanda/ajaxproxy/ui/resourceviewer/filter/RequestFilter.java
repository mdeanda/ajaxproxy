package com.thedeanda.ajaxproxy.ui.resourceviewer.filter;

import java.util.List;
import java.util.regex.Pattern;

import com.thedeanda.ajaxproxy.ui.model.Resource;

public class RequestFilter {
	private Pattern filterRegEx;
	private List<RequestType> items;

	public void setRequestTypes(List<RequestType> checkedItems) {
		this.items = checkedItems;
	}

	public boolean accept(Resource item) {
		if (filterRegEx != null && !filterRegEx.matcher(item.getPath()).matches()) {
			return false;
		}

		boolean accept = true;
		if (items != null && !items.isEmpty()) {
			accept = false;
			for (RequestType rt : items) {
				if (rt.isMethod()) {
					if (rt.name().equalsIgnoreCase(item.getMethod())) {
						accept = true;
					}
				} else if (rt.isStatusCode()) {
					if (rt.isStatusInRange(item.getStatus())) {
						accept = true;
					}
				}
			}
		}
		return accept;
	}

	public void setRegEx(Pattern filterRegEx) {
		this.filterRegEx = filterRegEx;
	}
}
