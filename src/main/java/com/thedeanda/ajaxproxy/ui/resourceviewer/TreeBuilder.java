package com.thedeanda.ajaxproxy.ui.resourceviewer;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Iterator;

public class TreeBuilder {
	private static final int MAX_WIDTH = 80;

	public DefaultMutableTreeNode buildTree(JsonObject object) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(getNodeValue(null, object));
		initTree(rootNode, object);
		return rootNode;
	}

	public DefaultMutableTreeNode buildTree(JsonArray array) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(getNodeValue(null, array));
		initTree(rootNode, array);
		return rootNode;
	}

	public DefaultMutableTreeNode buildTree(Document doc) {
		DefaultMutableTreeNode rootNode = initTree(doc);
		return rootNode;
	}

	private String getNodeValue(String fieldName, JsonObject json) {
		StringBuilder output = new StringBuilder();
		if (StringUtils.isNotBlank(fieldName)) {
			output.append(fieldName);
			output.append(": ");
		}
		output.append("{");
		output.append(json.size());
		output.append("} ");
		output.append(StringUtils.abbreviate(json.toString(), MAX_WIDTH));

		return output.toString().trim();
	}

	private String getNodeValue(String fieldName, JsonArray json) {
		StringBuilder output = new StringBuilder();
		if (StringUtils.isNotBlank(fieldName)) {
			output.append(fieldName);
			output.append(": ");
		}
		output.append("[");
		output.append(json.size());
		output.append("] ");
		output.append(StringUtils.abbreviate(json.toString(), MAX_WIDTH));

		return output.toString().trim();
	}

	private void initTree(DefaultMutableTreeNode top, JsonObject obj) {
		for (String key : obj) {
			JsonValue val = obj.get(key);
			initTree(top, val, key);
		}
	}

	private void initTree(DefaultMutableTreeNode top, JsonArray arr) {
		int i = 0;
		for (JsonValue val : arr) {
			initTree(top, val, String.valueOf(i));
			i++;
		}
	}

	private void initTree(DefaultMutableTreeNode top, JsonValue val, String key) {
		if (val.isJsonObject()) {
			String name = getNodeValue(key, val.getJsonObject());
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
			top.add(node);
			initTree(node, val.getJsonObject());
		} else if (val.isJsonArray()) {
			String name = getNodeValue(key, val.getJsonArray());
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
			top.add(node);
			initTree(node, val.getJsonArray());
		} else {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "=" + val.toString());
			top.add(node);
		}
	}

	private DefaultMutableTreeNode initTree(Document doc) {
		Element rootEl = doc.getRootElement();
		DefaultMutableTreeNode rootNode = createElementNodes(rootEl);
		initTree(rootNode, rootEl);
		return rootNode;
	}

	@SuppressWarnings("rawtypes")
	private void initTree(DefaultMutableTreeNode root, Element element) {
		int count = 0;
		for (Iterator i = element.elementIterator(); i.hasNext();) {
			Element el = (Element) i.next();
			DefaultMutableTreeNode tmp = createElementNodes(el);
			if (tmp == null)
				continue;
			count++;
			root.add(tmp);
			initTree(tmp, el);

			String txt = el.getTextTrim();
			if (txt != null && !"".equals(txt)) {
				DefaultMutableTreeNode txtNode = new DefaultMutableTreeNode(txt);
				tmp.add(txtNode);
			}
		}
		if (count > 0) {
			String title = (String) root.getUserObject();
			title += " [" + count + "]";
			root.setUserObject(title);
		}
	}

	@SuppressWarnings("rawtypes")
	private DefaultMutableTreeNode createElementNodes(Element element) {
		String name = element.getName();
		if (name == null || "".equals(name))
			return null;
		DefaultMutableTreeNode ret = new DefaultMutableTreeNode(name);

		for (Iterator i = element.attributeIterator(); i.hasNext();) {
			Attribute attr = (Attribute) i.next();

			DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(attr.getName() + " = " + attr.getText());
			ret.add(tmp);
		}
		return ret;
	}

}
