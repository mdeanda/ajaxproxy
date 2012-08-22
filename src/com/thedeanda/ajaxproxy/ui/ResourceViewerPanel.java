package com.thedeanda.ajaxproxy.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.javajson.JsonArray;
import net.sourceforge.javajson.JsonException;
import net.sourceforge.javajson.JsonObject;
import net.sourceforge.javajson.JsonValue;

import org.apache.commons.lang.StringUtils;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.LoadedResource;

/** tracks files that get loaded */
public class ResourceViewerPanel extends JPanel implements AccessTracker,
		ActionListener {
	private static final long serialVersionUID = 1L;
	private static final int INPUT_TAB = 1;
	private static final int INPUT_FORMATTED_TAB = 2;
	private static final int OUTPUT_TAB = 3;
	private static final int OUTPUT_FORMATTED_TAB = 4;
	private JButton clearBtn;
	private JButton exportBtn;
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
	private JTextField filter;
	private Color okColor;
	private Color badColor;
	private Pattern filterRegEx;
	private JMenuItem removeRequestMenuItem;
	private JTree outputInteractive;
	private JScrollPane outputInteractiveScroll;
	private DefaultTreeModel outputTreeModel;
	private DefaultMutableTreeNode rootOutputNode;

	public ResourceViewerPanel() {
		setLayout(new MigLayout("fill", "[][][grow]", "[][fill]"));
		model = new DefaultListModel();

		clearBtn = new JButton("Clear");
		exportBtn = new JButton("Export");
		toggleBtn = new JCheckBox("Monitor Resources");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.clear();
				showResource(null);
			}
		});
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});

		add(clearBtn);
		add(exportBtn);
		add(toggleBtn, "align right, wrap");

		JPanel leftPanel = new JPanel(new MigLayout("insets 0, fill", "[fill]",
				""));
		leftPanel.setBorder(BorderFactory.createEmptyBorder());

		filter = new JTextField();
		leftPanel.add(filter, "growx, wrap");
		// Listen for changes in the text
		okColor = filter.getBackground();
		badColor = new Color(240, 220, 200);
		filter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				resetFilter();
			}
		});

		final JPopupMenu popup = new JPopupMenu();
		removeRequestMenuItem = new JMenuItem("Remove Request");
		removeRequestMenuItem.addActionListener(this);
		popup.add(removeRequestMenuItem);

		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				listItemSelected(evt);
			}
		});
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				list.setSelectedIndex(list.locationToIndex(e.getPoint()));
				if (e.isPopupTrigger() && list.getSelectedIndex() >= 0) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		list.setBorder(BorderFactory.createEmptyBorder());
		leftPanel.add(new JScrollPane(list), "grow");

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

		rootOutputNode = new DefaultMutableTreeNode("");
		outputTreeModel = new DefaultTreeModel(rootOutputNode);
		initTree(rootOutputNode, new JsonObject());
		outputInteractive = new JTree(outputTreeModel);
		outputInteractiveScroll = new JScrollPane(outputInteractive);
		outputInteractive.setShowsRootHandles(true);

		outputContent = new JTextArea();
		outputContent.setEditable(false);
		outputScroll = new JScrollPane(outputContent);

		outputFormattedContent = new JTextArea();
		outputFormattedContent.setEditable(false);
		outputFormattedScroll = new JScrollPane(outputFormattedContent);

		tabs = new JTabbedPane();
		tabs.add("Headers", headersScroll);
		tabs.add("Input", inputScroll);
		tabs.add("Input (formatted)", inputFormattedScroll);
		tabs.add("Output", outputScroll);
		tabs.add("Output (formatted)", outputFormattedScroll);
		tabs.add("Output (interactive)", outputInteractiveScroll);
		tabs.setBorder(BorderFactory.createEmptyBorder());

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(leftPanel);
		split.setRightComponent(tabs);
		split.setDividerLocation(200);
		split.setBorder(BorderFactory.createEmptyBorder());
		flattenSplitPane(split);
		add(split, "span 3, growx, growy");
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
		
		rootOutputNode.removeAllChildren();
		outputTreeModel.reload();

		if (lr != null) {
			final DataHolder holder = new DataHolder();
			final Runnable uiupdate = new Runnable() {
				@Override
				public void run() {
					headersContent.setText(holder.headers);
					headersContent.setCaretPosition(0);

					inputContent.setText(holder.input);
					inputContent.setCaretPosition(0);
					if (holder.input == null)
						tabs.setEnabledAt(INPUT_TAB, false);
					else
						tabs.setEnabledAt(INPUT_TAB, true);
					if (holder.inputFormatted != null) {
						inputFormattedContent.setText(holder.inputFormatted);
						inputFormattedContent.setCaretPosition(0);
						tabs.setEnabledAt(INPUT_FORMATTED_TAB, true);
					} else {
						tabs.setEnabledAt(INPUT_FORMATTED_TAB, false);
					}

					try {
						JsonObject obj = JsonObject.parse(holder.output);
						initTree(rootOutputNode, obj);
						outputTreeModel.reload();
					} catch (JsonException e) {

					}

					outputContent.setText(holder.output);
					outputContent.setCaretPosition(0);
					if (holder.output == null)
						tabs.setEnabledAt(OUTPUT_TAB, false);
					else
						tabs.setEnabledAt(OUTPUT_TAB, true);
					if (holder.outputFormatted != null) {
						outputFormattedContent.setText(holder.outputFormatted);
						outputFormattedContent.setCaretPosition(0);
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
		boolean show = toggleBtn.isSelected();
		if (show && filterRegEx != null) {
			Matcher matcher = filterRegEx.matcher(res.getUrl());
			if (matcher.matches())
				show = true;
			else
				show = false;
		}

		if (show) {
			model.addElement(res);
		}
	}

	private void resetFilter() {
		try {
			filterRegEx = Pattern.compile(filter.getText());
			filter.setBackground(okColor);
		} catch (PatternSyntaxException ex) {
			filterRegEx = null;
			filter.setBackground(badColor);
		}
	}

	private void export() {
		String urlPrefix = JOptionPane.showInputDialog("URL Prefix",
				"http://localhost");

		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			String path = folder.getAbsolutePath();
			for (int i = 0; i < model.getSize(); i++) {
				LoadedResource obj = (LoadedResource) model.get(i);
				String fn = StringUtils.leftPad(String.valueOf(i), 8, "0");
				JsonObject json = new JsonObject();
				json.put("url", urlPrefix + obj.getUrl());
				try {
					json.put("input", JsonObject.parse(obj.getInputAsText()));
				} catch (JsonException e1) {
					json.put("input", obj.getInputAsText());
					e1.printStackTrace();
				}
				try {
					json.put("output", JsonObject.parse(obj.getOutputAsText()));
				} catch (JsonException e1) {
					json.put("output", obj.getOutputAsText());
					e1.printStackTrace();
				}
				json.put("status", obj.getStatusCode());
				json.put("duration", obj.getDuration());
				json.put("method", obj.getMethod());
				JsonObject headers = new JsonObject();
				json.put("headers", headers);
				Map<String, String> hdrs = obj.getHeaders();
				for (String key : hdrs.keySet()) {
					headers.put(key, hdrs.get(key));
				}

				Writer writer = null;
				try {
					writer = new OutputStreamWriter(new FileOutputStream(
							new File(path + File.separator + fn + ".txt")),
							"UTF-8");
					writer.write(json.toString(4));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
	}

	class DataHolder {
		public String input;
		public String inputFormatted;
		public String output;
		public String outputFormatted;
		public String headers;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == removeRequestMenuItem) {
			int index = list.getSelectedIndex();
			if (index >= 0) {
				model.remove(index);
				if (model.getSize() > index)
					list.setSelectedIndex(index);
				else if (!model.isEmpty()) {
					list.setSelectedIndex(index - 1);
				}
			}
		}
	}
}
