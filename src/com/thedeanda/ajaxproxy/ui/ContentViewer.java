package com.thedeanda.ajaxproxy.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.javajson.JsonArray;
import net.sourceforge.javajson.JsonException;
import net.sourceforge.javajson.JsonObject;
import net.sourceforge.javajson.JsonValue;

import org.apache.log4j.Logger;

/**
 * this is a content viewer used to show input/ouput content of http requests.
 * it may have a nested tab view to switch between raw, formatted and tree view
 * 
 * @author mdeanda
 * 
 */
public class ContentViewer extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ContentViewer.class);
	private JTabbedPane tabs;

	public ContentViewer() {
		setLayout(new MigLayout("insets 10, fill", "[fill]", "[fill]"));
		tabs = new JTabbedPane();
		add(tabs);
		tabs.add("foo", new JButton("foo"));

		setBorder(BorderFactory.createEmptyBorder());
		tabs.setBorder(BorderFactory.createEmptyBorder());
	}

	public void setContent(final String output) {
		log.info("setting content");
		tabs.removeAll();

		new Thread(new Runnable() {
			@Override
			public void run() {
				TreeNode node = null;
				String formattedText = null;
				if (output != null) {
					try {
						JsonObject json = JsonObject.parse(output);
						DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
								"");
						initTree(rootNode, json);
						node = rootNode;
					} catch (Exception e) {
					}

					formattedText = tryFormatting(output);
				}

				final TreeNode rootNode = node;
				final String formatted = formattedText;

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (output != null) {
							tabs.add("Raw Text", new JScrollPane(new JTextArea(
									output)));
						}
						if (formatted != null) {
							tabs.add("Formatted", new JScrollPane(
									new JTextArea(formatted)));
						}

						if (rootNode != null) {
							JTree tree = new JTree(new DefaultTreeModel(
									rootNode));
							tree.setBorder(BorderFactory.createEmptyBorder());
							tree.setShowsRootHandles(true);
							JScrollPane scroll = new JScrollPane(tree);
							scroll.setBorder(BorderFactory.createEmptyBorder());
							tabs.add("Tree View", scroll);
						}

					}
				});
			}
		}).start();
	}

	private void initTree(DefaultMutableTreeNode top, JsonObject obj) {
		for (String key : obj) {
			JsonValue val = obj.get(key);
			if (val.isJsonObject()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
				top.add(node);
				initTree(node, val.getJsonObject());
			} else if (val.isJsonArray()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
				top.add(node);
				initTree(node, val.getJsonArray());
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(key
						+ "=" + val.toString());
				top.add(node);
			}
		}
	}

	private void initTree(DefaultMutableTreeNode top, JsonArray arr) {
		int i = 0;
		for (JsonValue val : arr) {
			if (val.isJsonObject()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode("["
						+ i + "]");
				top.add(node);
				initTree(node, val.getJsonObject());
			} else if (val.isJsonArray()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode("["
						+ i + "]");
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

	private String tryFormatting(String str) {
		String ret = null;
		if (str == null)
			return null;

		str = str.trim();
		if (str.startsWith("{") || str.startsWith("[")) {
			// try json parsing
			try {
				ret = JsonObject.parse(str).toString(4);
			} catch (JsonException je) {
				ret = null;
			}
		} else if (str.startsWith("<")) {
			// try xml parsing
			ret = null;
		}
		return ret;
	}
}
