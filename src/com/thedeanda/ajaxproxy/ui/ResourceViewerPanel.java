package com.thedeanda.ajaxproxy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.javajson.JsonException;
import net.sourceforge.javajson.JsonObject;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.LoadedResource;

/** tracks files that get loaded */
public class ResourceViewerPanel extends JPanel implements AccessTracker {
	private static final long serialVersionUID = 1L;
	private JButton clearBtn;
	private JCheckBox toggleBtn;
	private DefaultListModel model;
	private JList list;
	private JTabbedPane tabs;
	private JTextArea outputContent;
	private JScrollPane outputScroll;
	private JTextArea inputContent;
	private JScrollPane inputScroll;
	private JEditorPane headersContent;
	private JScrollPane headersScroll;

	public ResourceViewerPanel() {
		this.setLayout(new MigLayout("fill", "", "[][fill]"));
		model = new DefaultListModel();

		this.clearBtn = new JButton("Clear");
		toggleBtn = new JCheckBox("Monitor Resources");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.clear();
				showResource(null);
			}
		});

		add(clearBtn);
		add(toggleBtn, "align right, wrap");

		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				listItemSelected(evt);
			}
		});

		HTMLEditorKit kit = new HTMLEditorKit();
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet
				.addRule("body {color:#000000; margin: 4px; font-size: 10px; font-family: sans-serif; }");
		styleSheet.addRule("h1 { margin: 4px 0; font-size: 12px; }");
		styleSheet.addRule("div.items { margin-left: 10px;}");
		styleSheet.addRule("p { margin: 0; font-family: monospace;}");
		styleSheet.addRule("b { font-family: sans-serif; color: #444444;}");

		headersContent = new JEditorPane();
		headersContent.setEditable(false);
		headersScroll = new JScrollPane(headersContent);
		headersContent.setEditorKit(kit);
		Document doc = kit.createDefaultDocument();
		headersContent.setDocument(doc);

		inputContent = new JTextArea();
		inputContent.setEditable(false);
		inputScroll = new JScrollPane(inputContent);

		outputContent = new JTextArea();
		outputContent.setEditable(false);
		outputScroll = new JScrollPane(outputContent);

		tabs = new JTabbedPane();
		tabs.add("Headers", headersScroll);
		tabs.add("Input", inputScroll);
		tabs.add("Output", outputScroll);
		tabs.setBorder(BorderFactory.createEmptyBorder());

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JScrollPane(list));
		split.setRightComponent(tabs);
		split.setDividerLocation(200);
		split.setBorder(BorderFactory.createEmptyBorder());
		flattenSplitPane(split);
		add(split, "span 2, growx, growy");
	}

	public static void flattenSplitPane(JSplitPane jSplitPane) {
		jSplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {
					}
				};
			}
		});
		jSplitPane.setBorder(null);
	}

	private void listItemSelected(ListSelectionEvent evt) {
		if (!evt.getValueIsAdjusting()) {
			LoadedResource lr = (LoadedResource) list.getSelectedValue();
			if (lr != null) {
				showResource(lr);
			}
		}
	}

	private void showResource(LoadedResource lr) {
		if (lr == null) {
			outputContent.setText("");
			inputContent.setText("");
			headersContent.setText("");
		} else {
			outputContent.setText(tryFormatting(lr.getOutputAsText()));
			inputContent.setText(tryFormatting(lr.getInputAsText()));

			StringBuffer headers = new StringBuffer();

			Map<String, String> map = lr.getHeaders();
			headers.append("<html><body>");
			headers.append("<p><b>URL:</b> ");
			headers.append(lr.getUrl());
			headers.append("</p>");
			headers.append("<p><b>Method:</b> ");
			headers.append(lr.getMethod());
			headers.append("</p>");
			headers.append("<p><b>Duration:</b> ");
			headers.append(lr.getDuration());
			headers.append("</p>");
			writeField(headers, "Status", String.valueOf(lr.getStatusCode()));
			headers.append("<h1>Headers</h1><div class=\"items\">");
			for (String name : map.keySet()) {
				headers.append("<p><b>");
				headers.append(name);
				headers.append(":</b> ");
				headers.append(map.get(name));
				headers.append("</p>");
			}
			headers.append("</div></body></html>");

			headersContent.setText(headers.toString());
		}
	}

	private void writeField(StringBuffer headers, String name, String value) {
		headers.append("<p><b>");
		headers.append(name);
		headers.append(":</b> ");
		headers.append(value);
		headers.append("</p>");
	}

	private String tryFormatting(String str) {
		String ret = str;
		if (str == null)
			return null;

		str = str.trim();
		if (str.startsWith("{") || str.startsWith("[")) {
			// try json parsing
			try {
				ret = JsonObject.parse(ret).toString(4);
			} catch (JsonException je) {
				ret = str;
			}
		} else if (str.startsWith("<")) {
			// try xml parsing
		}
		return ret;
	}

	public void setProxy(AjaxProxy proxy) {
		if (proxy != null)
			proxy.addTracker(this);
	}

	@Override
	public void trackFile(LoadedResource res) {
		if (toggleBtn.isSelected()) {
			model.addElement(res);
		}
	}

}
