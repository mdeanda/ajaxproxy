package com.thedeanda.ajaxproxy.ui;

import java.awt.Component;
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
import javax.swing.SwingUtilities;
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
	private static final int INPUT_TAB = 1;
	private static final int INPUT_FORMATTED_TAB = 2;
	private static final int OUTPUT_TAB = 3;
	private static final int OUTPUT_FORMATTED_TAB = 4;
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
	private JTextArea inputFormattedContent;
	private JScrollPane inputFormattedScroll;
	private JTextArea outputFormattedContent;
	private JScrollPane outputFormattedScroll;
	private Component inputFormattedTab;

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
		inputFormattedContent = new JTextArea();
		inputFormattedContent.setEditable(false);
		inputFormattedScroll = new JScrollPane(inputFormattedContent);

		outputContent = new JTextArea();
		outputContent.setEditable(false);
		outputScroll = new JScrollPane(outputContent);

		outputFormattedContent = new JTextArea();
		outputFormattedContent.setEditable(false);
		outputFormattedScroll = new JScrollPane(outputFormattedContent);

		tabs = new JTabbedPane();
		tabs.add("Headers", headersScroll);
		tabs.add("Input", inputScroll);
		inputFormattedTab = tabs.add("Input (formatted)", inputFormattedScroll);
		tabs.add("Output", outputScroll);
		tabs.add("Output (formatted)", outputFormattedScroll);
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

	private void showResource(final LoadedResource lr) {
		outputContent.setText("");
		inputContent.setText("");
		headersContent.setText("");
		inputFormattedContent.setText("");
		outputFormattedContent.setText("");

		if (lr != null) {
			final DataHolder holder = new DataHolder();
			final Runnable uiupdate = new Runnable() {
				@Override
				public void run() {
					headersContent.setText(holder.headers);

					inputContent.setText(holder.input);
					if (holder.input == null)
						tabs.setEnabledAt(INPUT_TAB, false);
					else
						tabs.setEnabledAt(INPUT_TAB, true);
					if (holder.inputFormatted != null) {
						inputFormattedContent.setText(holder.inputFormatted);
						tabs.setEnabledAt(INPUT_FORMATTED_TAB, true);
					} else {
						tabs.setEnabledAt(INPUT_FORMATTED_TAB, false);
					}

					outputContent.setText(holder.output);
					if (holder.output == null)
						tabs.setEnabledAt(OUTPUT_TAB, false);
					else
						tabs.setEnabledAt(OUTPUT_TAB, true);
					if (holder.outputFormatted != null) {
						outputFormattedContent.setText(holder.outputFormatted);
						tabs.setEnabledAt(OUTPUT_FORMATTED_TAB, true);
					} else {
						tabs.setEnabledAt(OUTPUT_FORMATTED_TAB, false);
					}
				}
			};
			new Thread(new Runnable() {
				@Override
				public void run() {
					holder.input = lr.getInputAsText();
					if (holder.input != null && holder.input.trim().equals(""))
						holder.input = null;
					holder.inputFormatted = tryFormatting(holder.input);
					
					holder.output = lr.getOutputAsText();
					if (holder.output != null
							&& holder.output.trim().equals(""))
						holder.output = null;
					holder.outputFormatted = tryFormatting(holder.output);

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
					writeField(headers, "Status",
							String.valueOf(lr.getStatusCode()));
					headers.append("<h1>Headers</h1><div class=\"items\">");
					for (String name : map.keySet()) {
						headers.append("<p><b>");
						headers.append(name);
						headers.append(":</b> ");
						headers.append(map.get(name));
						headers.append("</p>");
					}
					headers.append("</div></body></html>");

					holder.headers = headers.toString();
					SwingUtilities.invokeLater(uiupdate);
				}
			}).start();
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

	class DataHolder {
		public String input;
		public String inputFormatted;
		public String output;
		public String outputFormatted;
		public String headers;
	}
}
