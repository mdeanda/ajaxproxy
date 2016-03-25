package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

/**
 * this is a content viewer used to show input/ouput content of http requests.
 * it may have a nested tab view to switch between raw, formatted and tree view
 * 
 * @author mdeanda
 * 
 */
public class ContentViewer extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(ContentViewer.class);
	private JTabbedPane tabs;

	public ContentViewer() {
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		add(BorderLayout.CENTER, tabs);
		tabs.add("", new JButton(""));

		setBorder(BorderFactory.createEmptyBorder());
		tabs.setBorder(BorderFactory.createEmptyBorder());
	}

	private class WorkerData {
		public String rawText;
		public String formattedText;
		public TreeNode treeNode;
	}

	public void setContent(final String input) {
		log.trace("setting content");
		tabs.removeAll();

		if (StringUtils.isBlank(input)) {
			return;
		}

		new TreeLoader(input).execute();
	}

	private class TreeLoader extends SwingWorker<WorkerData, WorkerData> {

		private String input;

		public TreeLoader(String input) {
			this.input = input;
		}

		@Override
		protected WorkerData doInBackground() throws Exception {
			WorkerData data = new WorkerData();
			data.rawText = input;

			TreeNode node = null;
			String formattedText = null;
			Document doc = null;
			if (input != null) {
				if (input.trim().startsWith("{")) {
					try {
						JsonObject json = JsonObject.parse(input);
						DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
								"{}");
						initTree(rootNode, json);
						node = rootNode;

						formattedText = json.toString(4);
					} catch (Exception e) {
						log.trace(e.getMessage(), e);
					}
				}
				if (formattedText == null && input.trim().startsWith("[")) {
					try {
						JsonArray json = JsonArray.parse(input);
						DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
								"[]");
						initTree(rootNode, json);
						node = rootNode;

						formattedText = json.toString(4);
					} catch (Exception e) {
						log.trace(e.getMessage(), e);
					}
				}

				if (formattedText == null && !"".equals(formattedText)) {
					if (input.trim().startsWith("<")) {
						// try xml formatting
						try {
							doc = DocumentHelper.parseText(input);
							node = initTree(doc);
							formattedText = formatXml(doc);
						} catch (DocumentException e) {
							log.trace(e.getMessage(), e);
						}
					}
				}
			}
			data.formattedText = formattedText;
			data.treeNode = node;

			return data;
		}

		@Override
		protected void done() {
			WorkerData data;
			try {
				data = get();
			} catch (InterruptedException | ExecutionException e) {
				log.error(e.getMessage(), e);
				return;
			}

			if (data.rawText != null) {
				tabs.add("Raw Text", new JScrollPane(
						new JTextArea(data.rawText)));
			}
			if (data.formattedText != null) {
				tabs.add("Formatted", new JScrollPane(new JTextArea(
						data.formattedText)));
			}

			if (data.treeNode != null) {
				JTree tree = new JTree(new DefaultTreeModel(data.treeNode));
				tree.setBorder(BorderFactory.createEmptyBorder());
				tree.setShowsRootHandles(true);
				JScrollPane scroll = new JScrollPane(tree);
				scroll.setBorder(BorderFactory.createEmptyBorder());
				tabs.add("Tree View", scroll);
			}
			if (tabs.getTabCount() > 1) {
				tabs.setSelectedIndex(1);
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
			for (Iterator i = element.elementIterator(); i.hasNext();) {
				Element el = (Element) i.next();
				DefaultMutableTreeNode tmp = createElementNodes(el);
				if (tmp == null)
					continue;
				root.add(tmp);
				initTree(tmp, el);

				String txt = el.getTextTrim();
				if (txt != null && !"".equals(txt)) {
					DefaultMutableTreeNode txtNode = new DefaultMutableTreeNode(
							txt);
					tmp.add(txtNode);
				}
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

				DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(
						attr.getName() + " = " + attr.getText());
				ret.add(tmp);
			}
			return ret;
		}

		private void initTree(DefaultMutableTreeNode top, JsonObject obj) {
			for (String key : obj) {
				JsonValue val = obj.get(key);
				if (val.isJsonObject()) {
					String name = String.format("%s: {%d}", key, val
							.getJsonObject().size());
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							name);
					top.add(node);
					initTree(node, val.getJsonObject());
				} else if (val.isJsonArray()) {
					String name = String.format("%s: [%d]", key, val
							.getJsonArray().size());
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							name);
					top.add(node);
					initTree(node, val.getJsonArray());
				} else {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							key + "=" + val.toString());
					top.add(node);
				}
			}
		}

		private void initTree(DefaultMutableTreeNode top, JsonArray arr) {
			int i = 0;
			for (JsonValue val : arr) {
				if (val.isJsonObject()) {
					String name = String.format("%s: {%d}", String.valueOf(i),
							val.getJsonObject().size());
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							name);
					top.add(node);
					initTree(node, val.getJsonObject());
				} else if (val.isJsonArray()) {
					String name = String.format("%s: [%d]", i, val
							.getJsonArray().size());
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							name);
					top.add(node);
					initTree(node, val.getJsonArray());
				} else {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							val.toString());
					top.add(node);
				}
				i++;
			}
		}

		private String formatXml(Document doc) {
			StringWriter out = new StringWriter();
			try {
				OutputFormat outformat = OutputFormat.createPrettyPrint();
				outformat.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(out, outformat);
				writer.write(doc);
				writer.flush();
			} catch (Exception e) {
			}
			return out.toString();
		}
	}

}
