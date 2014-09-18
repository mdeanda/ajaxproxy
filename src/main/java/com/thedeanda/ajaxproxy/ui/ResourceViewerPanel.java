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
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.javajson.JsonArray;
import net.sourceforge.javajson.JsonException;
import net.sourceforge.javajson.JsonObject;
import net.sourceforge.javajson.JsonValue;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.LoadedResource;

/** tracks files that get loaded */
public class ResourceViewerPanel extends JPanel implements AccessTracker,
		ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton clearBtn;
	private JButton exportBtn;
	private JCheckBox toggleBtn;
	private DefaultListModel<LoadedResource> model;
	private JList<LoadedResource> list;
	private ResourcePanel resourcePanel;
	private JTextField filter;
	private Color okColor;
	private Color badColor;
	private Pattern filterRegEx;
	private JMenuItem removeRequestMenuItem;
	private JMenuItem replyMenuItem;
	private AjaxProxy ajaxProxy;

	public ResourceViewerPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		model = new DefaultListModel<LoadedResource>();

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
		add(toggleBtn);

		JPanel leftPanel = initLeftPanel();

		resourcePanel = new ResourcePanel(false);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(leftPanel);
		split.setRightComponent(resourcePanel);
		split.setDividerLocation(200);
		split.setBorder(BorderFactory.createEmptyBorder());
		SwingUtils.flattenSplitPane(split);
		add(split);

		layout.putConstraint(SpringLayout.NORTH, clearBtn, 20,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, clearBtn, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, exportBtn, 0,
				SpringLayout.NORTH, clearBtn);
		layout.putConstraint(SpringLayout.WEST, exportBtn, 10,
				SpringLayout.EAST, clearBtn);
		layout.putConstraint(SpringLayout.NORTH, toggleBtn, 20,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, toggleBtn, -10,
				SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, split, 30, SpringLayout.SOUTH,
				clearBtn);
		layout.putConstraint(SpringLayout.SOUTH, split, -10,
				SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, split, -10, SpringLayout.EAST,
				this);

	}

	private JPanel initLeftPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel leftPanel = new JPanel(layout);
		leftPanel.setBorder(BorderFactory.createEmptyBorder());

		filter = new JTextField();
		SwingUtils.prepJTextField(filter);
		leftPanel.add(filter);

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

		replyMenuItem = new JMenuItem("Replay Request");
		replyMenuItem.addActionListener(this);
		popup.add(replyMenuItem);

		list = new JList<LoadedResource>(model);
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
		JScrollPane scroll = new JScrollPane(list);
		leftPanel.add(scroll);

		layout.putConstraint(SpringLayout.NORTH, filter, 0, SpringLayout.NORTH,
				leftPanel);
		layout.putConstraint(SpringLayout.WEST, filter, 10, SpringLayout.WEST,
				leftPanel);
		layout.putConstraint(SpringLayout.EAST, filter, 0, SpringLayout.EAST,
				leftPanel);

		layout.putConstraint(SpringLayout.NORTH, scroll, 10,
				SpringLayout.SOUTH, filter);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST,
				leftPanel);
		layout.putConstraint(SpringLayout.EAST, scroll, 0, SpringLayout.EAST,
				leftPanel);
		layout.putConstraint(SpringLayout.SOUTH, scroll, 0, SpringLayout.SOUTH,
				leftPanel);

		return leftPanel;
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

	private void listItemSelected(ListSelectionEvent evt) {
		if (!evt.getValueIsAdjusting()) {
			LoadedResource lr = (LoadedResource) list.getSelectedValue();
			if (lr != null) {
				showResource(lr);
			}
		}
	}

	private void showResource(final LoadedResource lr) {
		resourcePanel.setResource(lr);
	}

	public void setProxy(AjaxProxy ajaxProxy) {
		this.ajaxProxy = ajaxProxy;
		if (ajaxProxy != null)
			ajaxProxy.addTracker(this);
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

		if (urlPrefix == null) {
			return;
		}

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

	public JsonObject getConfig() {
		JsonObject data = new JsonObject();
		data.put("track", toggleBtn.isSelected());
		return data;
	}

	public void setConfig(JsonObject config) {
		if (config == null)
			return;
		toggleBtn.setSelected(config.getBoolean("track"));
	}

	class DataHolder {
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
		} else if (evt.getSource() == replyMenuItem) {
			int index = list.getSelectedIndex();
			if (index >= 0) {
				final LoadedResource resource = model.get(index);
				SwingUtils.executNonUi(new Runnable() {
					@Override
					public void run() {
						ajaxProxy.replay(resource);
					}
				});
			}
		}
	}
}
