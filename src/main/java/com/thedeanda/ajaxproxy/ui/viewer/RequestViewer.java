package com.thedeanda.ajaxproxy.ui.viewer;

import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.http.Header;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.NetworkUtil;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ContentViewer;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

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
	private static final Logger log = LoggerFactory.getLogger(RequestViewer.class);
	private JTextArea headersField;
	private JLabel dataLabel;
	private JScrollPane headerScroll;
	private JTextField statusCode;
	private JTextField statusPhrase;
	private JTextField durationField;
	private ContentViewer contentViewer;

	public RequestViewer() {
		super();
		setLayout(new BorderLayout());

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		SwingUtils.flattenSplitPane(split);
		split.setTopComponent(initHeaders());
		split.setBottomComponent(initTabs());
		split.setDividerLocation(250);
		add(split, BorderLayout.CENTER);

	}

	private JPanel initHeaders() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JLabel statusLabel = new JLabel("Status");
		panel.add(statusLabel);
		statusCode = SwingUtils.newJTextField();
		statusCode.setEditable(false);
		panel.add(statusCode);

		statusPhrase = SwingUtils.newJTextField();
		statusPhrase.setEditable(false);
		panel.add(statusPhrase);

		durationField = SwingUtils.newJTextField();
		durationField.setEditable(false);
		panel.add(durationField);

		JLabel headersLabel = new JLabel("Headers");
		headersField = SwingUtils.newJTextArea();
		headersField.setEditable(false);
		headerScroll = new JScrollPane(headersField);
		panel.add(headersLabel);
		panel.add(headerScroll);

		// status label
		layout.putConstraint(SpringLayout.WEST, statusLabel, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, statusLabel, 24, SpringLayout.NORTH, panel);

		// status label code
		layout.putConstraint(SpringLayout.WEST, statusCode, 20, SpringLayout.EAST, statusLabel);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, statusCode, 0, SpringLayout.VERTICAL_CENTER, statusLabel);
		layout.putConstraint(SpringLayout.EAST, statusCode, 90, SpringLayout.EAST, statusLabel);

		// duration
		layout.putConstraint(SpringLayout.WEST, durationField, -95, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, durationField, 0, SpringLayout.NORTH, statusCode);
		layout.putConstraint(SpringLayout.SOUTH, durationField, 0, SpringLayout.SOUTH, statusCode);
		layout.putConstraint(SpringLayout.EAST, durationField, -10, SpringLayout.EAST, panel);

		// headers label
		layout.putConstraint(SpringLayout.WEST, headersLabel, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, headersLabel, 15, SpringLayout.SOUTH, statusLabel);

		// status label phrase
		layout.putConstraint(SpringLayout.WEST, statusPhrase, 20, SpringLayout.EAST, statusCode);
		layout.putConstraint(SpringLayout.NORTH, statusPhrase, 0, SpringLayout.NORTH, statusCode);
		layout.putConstraint(SpringLayout.SOUTH, statusPhrase, 0, SpringLayout.SOUTH, statusCode);
		layout.putConstraint(SpringLayout.EAST, statusPhrase, -10, SpringLayout.WEST, durationField);

		// headers field
		layout.putConstraint(SpringLayout.WEST, headerScroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, headerScroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, headerScroll, 10, SpringLayout.SOUTH, headersLabel);
		layout.putConstraint(SpringLayout.SOUTH, headerScroll, -5, SpringLayout.SOUTH, panel);

		return panel;
	}

	private JPanel initTabs() {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		dataLabel = SwingUtils.newJLabel("Data");
		panel.add(dataLabel);
		contentViewer = new ContentViewer();
		panel.add(contentViewer);
		contentViewer.setBorder(BorderFactory.createEmptyBorder());

		// data label
		layout.putConstraint(SpringLayout.WEST, dataLabel, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, dataLabel, 5, SpringLayout.NORTH, panel);

		// data tabs
		layout.putConstraint(SpringLayout.WEST, contentViewer, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, contentViewer, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, contentViewer, 10, SpringLayout.SOUTH, dataLabel);
		layout.putConstraint(SpringLayout.SOUTH, contentViewer, -10, SpringLayout.SOUTH, panel);

		return panel;
	}

	@Override
	public void startRequest(UUID id, URL url, Header[] requestHeaders, byte[] data) {
		log.info("new request: {} {} {}", id, url, requestHeaders);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				clear();
			}
		});
	}

	@Override
	public void requestComplete(UUID id, final int status, final String reason, final long duration,
			Header[] responseHeaders, final byte[] data) {
		log.info("request complete: {} {} {}", id, status, responseHeaders);

		final byte[] outputData = NetworkUtil.decompressIfNeeded(data, responseHeaders);

		// String contentType = null;
		final StringBuilder headers = new StringBuilder();
		if (responseHeaders != null) {
			for (Header h : responseHeaders) {
				headers.append(String.format("%s: %s\n", h.getName(), h.getValue()));

				/*
				 * if ("content-type".equalsIgnoreCase(h.getName())) {
				 * contentType = h.getValue(); }
				 */
			}
		}

		// final ParsedData parsedData = new ParsedData();
		// parsedData.parse(outputData, contentType);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				headersField.setText(headers.toString().trim());
				scrollUp(headerScroll);
				statusCode.setText(String.valueOf(status));
				statusPhrase.setText(reason);
				durationField.setText(String.format("%d ms", duration));

				contentViewer.setContent(data);
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
				String name = String.format("%s: {%d}", key, val.getJsonObject().size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				top.add(node);
				initTree(node, val.getJsonObject());
			} else if (val.isJsonArray()) {
				String name = String.format("%s: [%d]", key, val.getJsonArray().size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				top.add(node);
				initTree(node, val.getJsonArray());
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(key + "=" + val.toString());
				top.add(node);
			}
		}
	}

	private void initTree(DefaultMutableTreeNode top, JsonArray arr) {
		int i = 0;
		for (JsonValue val : arr) {
			if (val.isJsonObject()) {
				String name = String.format("%s: {%d}", String.valueOf(i), val.getJsonObject().size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				top.add(node);
				initTree(node, val.getJsonObject());
			} else if (val.isJsonArray()) {
				String name = String.format("%s: [%d]", i, val.getJsonArray().size());
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				top.add(node);
				initTree(node, val.getJsonArray());
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(val.toString());
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

			DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(attr.getName() + " = " + attr.getText());
			ret.add(tmp);
		}
		return ret;
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		clear();

		if (e != null) {
			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			e.printStackTrace(writer);
			contentViewer.setContent(sw.toString().getBytes());
		}
	}

	private void clear() {
		headersField.setText("");
		contentViewer.setContent((byte[]) null);
		statusCode.setText("");
		statusPhrase.setText("");
		durationField.setText("");
	}

	@Override
	public void newRequest(UUID id, String url, String method) {
		clear();
	}
}
