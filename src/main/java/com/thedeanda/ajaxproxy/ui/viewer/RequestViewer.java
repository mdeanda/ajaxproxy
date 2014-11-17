package com.thedeanda.ajaxproxy.ui.viewer;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
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

import net.sourceforge.javajson.JsonArray;
import net.sourceforge.javajson.JsonObject;
import net.sourceforge.javajson.JsonValue;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.SwingUtils;

/**
 * this panel is the new implementation of the resource viewer tab. it will be
 * more event driven to allow it to monitor multiple requests as they are
 * happening instead of waiting for them to complete, possibly displaying them
 * out of order.
 * 
 * @author mdeanda
 *
 */
public class RequestViewer extends JPanel implements RequestListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(RequestViewer.class);
	private JTextArea headersField;
	private JLabel dataLabel;
	private JTabbedPane dataTabs;
	private JScrollPane headerScroll;

	public RequestViewer() {
		super();
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JLabel headersLabel = SwingUtils.newJLabel("Headers");
		headersField = SwingUtils.newJTextArea();
		headersField.setEditable(false);
		headerScroll = new JScrollPane(headersField);
		add(headersLabel);
		add(headerScroll);

		dataLabel = SwingUtils.newJLabel("Data");
		add(dataLabel);
		dataTabs = new JTabbedPane();
		add(dataTabs);
		dataTabs.setBorder(BorderFactory.createEmptyBorder());

		// headers label
		layout.putConstraint(SpringLayout.WEST, headersLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, headersLabel, 20,
				SpringLayout.NORTH, this);

		// headers field
		layout.putConstraint(SpringLayout.WEST, headerScroll, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, headerScroll, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, headerScroll, 20,
				SpringLayout.SOUTH, headersLabel);
		layout.putConstraint(SpringLayout.SOUTH, headerScroll, 123,
				SpringLayout.SOUTH, headersLabel);

		// data label
		layout.putConstraint(SpringLayout.WEST, dataLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, dataLabel, 20,
				SpringLayout.SOUTH, headerScroll);

		// data tabs
		layout.putConstraint(SpringLayout.WEST, dataTabs, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, dataTabs, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, dataTabs, 20,
				SpringLayout.SOUTH, dataLabel);
		layout.putConstraint(SpringLayout.SOUTH, dataTabs, -10,
				SpringLayout.SOUTH, this);
	}

	@Override
	public void newRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {
		log.info("new request: {} {} {}", id, url, requestHeaders);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				headersField.setText("");
				dataTabs.removeAll();
			}
		});
	}

	@Override
	public void requestComplete(UUID id, int status, Header[] responseHeaders,
			byte[] data) {
		log.info("request complete: {} {} {}", id, status, responseHeaders);

		String contentType = null;
		final StringBuilder headers = new StringBuilder();
		if (responseHeaders != null) {
			for (Header h : responseHeaders) {
				headers.append(String.format("%s: %s\n", h.getName(),
						h.getValue()));

				if ("content-type".equalsIgnoreCase(h.getName())) {
					contentType = h.getValue();
				}
			}
		}

		final ParsedData parsedData = new ParsedData();
		parsedData.parse(data, contentType);

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
		}

		final TreeNode rootNode = node;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				headersField.setText(headers.toString().trim());
				scrollUp(headerScroll);

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
					JTextArea txtField = SwingUtils.newJTextArea();
					JScrollPane scroll = new JScrollPane(txtField);
					txtField.setText(parsedData.formattedText);
					txtField.setEditable(false);
					txtField.setWrapStyleWord(true);
					txtField.setLineWrap(true);
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
			}
		});
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
}
