package com.thedeanda.ajaxproxy.model.tamper;

import com.thedeanda.javajson.JsonObject;

public class TamperConvertor {
	private static final String SELECTOR_NAME = "name";
	private static final String SELECTOR = "selector";
	private static final String SELECTOR_PATH = "path";

	public JsonObject convert(TamperItem element) {
		if (element == null)
			return null;

		JsonObject obj = new JsonObject();
		obj.put(SELECTOR, convert(element.getSelector()));

		return obj;
	}

	public TamperItem convertToElement(JsonObject json) {
		if (json == null)
			return null;

		TamperItem element = new TamperItem();
		element.setSelector(convertToSelector(json.getJsonObject(SELECTOR)));

		return element;
	}

	public JsonObject convert(TamperSelector selector) {
		if (selector == null)
			return null;

		JsonObject json = new JsonObject();
		json.put(SELECTOR_NAME, selector.getName());
		json.put(SELECTOR_PATH, selector.getPathRegEx());
		return json;
	}

	public TamperSelector convertToSelector(JsonObject json) {
		if (json == null)
			return null;

		TamperSelector selector = new TamperSelector();
		selector.setName(json.getString(SELECTOR_NAME));
		selector.setPathRegEx(json.getString(SELECTOR_PATH));
		return selector;
	}
}
