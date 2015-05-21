package com.thedeanda.ajaxproxy.ui.docviewer;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.fife.ui.hex.swing.HexEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.viewer.ImageViewer;
import com.thedeanda.ajaxproxy.ui.viewer.ParsedData;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class DocumentViewerPanel extends JPanel {
	private static final Logger log = LoggerFactory
			.getLogger(DocumentViewerPanel.class);
	private static final long serialVersionUID = 1L;
	private JTabbedPane dataTabs;

	public DocumentViewerPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		dataTabs = new JTabbedPane();
		add(dataTabs);
		dataTabs.setBorder(BorderFactory.createEmptyBorder());

		layout.putConstraint(SpringLayout.WEST, dataTabs, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, dataTabs, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, dataTabs, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, dataTabs, -10,
				SpringLayout.SOUTH, this);
	}

	private void clear() {
		dataTabs.removeAll();
	}

	public void interpretStringData(String input) {
		log.info("parse input: {}", input);
		clear();
		final ParsedData parsedData = new ParsedData();
		parsedData.parseString(input);
		updateTabs(parsedData);
	}

	private void updateTabs(final ParsedData parsedData) {
		TreeNode node = null;
		if (parsedData.json != null) {
			try {
				DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
						"{}");
				initTree(rootNode, parsedData.json);
				node = rootNode;
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		} else if (parsedData.jsonArray != null) {
			try {
				DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
						"[]");
				initTree(rootNode, parsedData.jsonArray);
				node = rootNode;
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		} else if (parsedData.xml != null) {
			try {
				node = initTree(parsedData.xml);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}

		final TreeNode rootNode = node;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int selectedTab = 0;
				byte[] data = parsedData.getData();

				if (!StringUtils.isBlank(parsedData.raw)) {
					JTextArea txtField = SwingUtils.newJTextArea();
					JScrollPane scroll = new JScrollPane(txtField);
					txtField.setText(parsedData.raw);
					txtField.setEditable(false);
					txtField.setWrapStyleWord(true);
					txtField.setLineWrap(true);
					dataTabs.add("Text", scroll);
					scrollUp(scroll);
				}
				if (!StringUtils.isBlank(parsedData.formattedText)) {
					selectedTab = 1;
					JTextArea txtField = SwingUtils.newJTextArea();
					JScrollPane scroll = new JScrollPane(txtField);
					txtField.setText(parsedData.formattedText);
					txtField.setEditable(false);
					txtField.setWrapStyleWord(true);
					txtField.setLineWrap(true);
					txtField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
					dataTabs.add("Formatted Text", scroll);
					scrollUp(scroll);
				}
				if (rootNode != null) {
					JTree tree = new JTree(new DefaultTreeModel(rootNode));
					tree.setBorder(BorderFactory.createEmptyBorder());
					tree.setShowsRootHandles(true);
					JScrollPane scroll = new JScrollPane(tree);
					scroll.setBorder(BorderFactory.createEmptyBorder());
					dataTabs.add("Tree View", scroll);
				}
				if (parsedData.bufferedImage != null) {
					ImageViewer panel = new ImageViewer(
							parsedData.bufferedImage);
					JScrollPane scroll = new JScrollPane(panel);
					scroll.setBorder(BorderFactory.createEmptyBorder());
					dataTabs.add("Image", scroll);
				}

				if (data != null) {
					try {
						HexEditor hex = new HexEditor();
						hex.open(new ByteArrayInputStream(data));
						hex.setCellEditable(false);
						dataTabs.add("Hex", hex);
					} catch (IOException e) {
						log.warn(e.getMessage(), e);
					}
				}
				if (selectedTab > 0) {
					dataTabs.setSelectedIndex(selectedTab);
				}
			}
		});
	}

	private void initTree(DefaultMutableTreeNode top, JsonObject obj) {
		for (String key : obj) {
			JsonValue val = obj.get(key);
			if (val.isJsonObject()) {
				String name = String.format("%s: {%d}", key, val
						.getJsonObject().size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				top.add(node);
				initTree(node, val.getJsonObject());
			} else if (val.isJsonArray()) {
				String name = String.format("%s: [%d]", key, val.getJsonArray()
						.size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
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
				String name = String.format("%s: {%d}", String.valueOf(i), val
						.getJsonObject().size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				top.add(node);
				initTree(node, val.getJsonObject());
			} else if (val.isJsonArray()) {
				String name = String.format("%s: [%d]", i, val.getJsonArray()
						.size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
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
				DefaultMutableTreeNode txtNode = new DefaultMutableTreeNode(txt);
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

	private void scrollUp(final JScrollPane scroll) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JScrollBar vscroll = scroll.getVerticalScrollBar();
				vscroll.setValue(vscroll.getMinimum());
			}
		});
	}
}
