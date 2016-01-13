package com.thedeanda.ajaxproxy.ui.resourceviewer;

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
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.LoadedResource;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
import com.thedeanda.ajaxproxy.ui.model.Resource;
import com.thedeanda.ajaxproxy.ui.model.ResourceListModel;
import com.thedeanda.ajaxproxy.ui.rest.RestClientFrame;
import com.thedeanda.ajaxproxy.ui.viewer.ResourceCellRenderer;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

/** tracks files that get loaded */
public class ResourceViewerPanel extends JPanel implements AccessTracker,
		ActionListener, RequestListener {
	private static final Logger log = LoggerFactory
			.getLogger(ResourceViewerPanel.class);
	private static final long serialVersionUID = 1L;
	private JButton clearBtn;
	private JButton exportBtn;
	private JCheckBox toggleBtn;
	private ResourceListModel model;
	private JList<Resource> list;
	private ResourcePanel resourcePanel;
	private JTextField filter;
	private Color filterOkColor;
	private Color filterBadColor;
	private JMenuItem removeRequestMenuItem;
	private JMenuItem replyMenuItem;
	private JMenuItem clearMenuItem;

	public ResourceViewerPanel() {
		log.debug("new viewer");
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		model = new ResourceListModel();

		resourcePanel = new ResourcePanel(false);
		clearBtn = new JButton("Clear");
		exportBtn = new JButton("Export");
		toggleBtn = new JCheckBox("Monitor Resources");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});

		JPanel topPanel = new JPanel();
		SpringLayout topPanelLayout = new SpringLayout();
		topPanel.setLayout(topPanelLayout);

		topPanel.add(clearBtn);
		topPanel.add(exportBtn);
		topPanel.add(toggleBtn);
		add(topPanel);
		topPanel.setBorder(new BottomBorder());

		JPanel leftPanel = initLeftPanel();
		JPanel rightPanel = initRightPanel();

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(leftPanel);
		split.setRightComponent(rightPanel);
		split.setDividerLocation(300);
		split.setBorder(BorderFactory.createEmptyBorder());
		SwingUtils.flattenSplitPane(split);
		add(split);

		topPanelLayout.putConstraint(SpringLayout.NORTH, clearBtn, 20,
				SpringLayout.NORTH, topPanel);
		topPanelLayout.putConstraint(SpringLayout.WEST, clearBtn, 10,
				SpringLayout.WEST, topPanel);
		topPanelLayout.putConstraint(SpringLayout.NORTH, exportBtn, 0,
				SpringLayout.NORTH, clearBtn);
		topPanelLayout.putConstraint(SpringLayout.WEST, exportBtn, 10,
				SpringLayout.EAST, clearBtn);
		topPanelLayout.putConstraint(SpringLayout.NORTH, toggleBtn, 20,
				SpringLayout.NORTH, topPanel);
		topPanelLayout.putConstraint(SpringLayout.EAST, toggleBtn, -10,
				SpringLayout.EAST, topPanel);

		layout.putConstraint(SpringLayout.NORTH, topPanel, 0,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, topPanel, 60,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST,
				this);

		layout.putConstraint(SpringLayout.NORTH, split, 0, SpringLayout.SOUTH,
				topPanel);
		layout.putConstraint(SpringLayout.SOUTH, split, 0, SpringLayout.SOUTH,
				this);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, split, 0, SpringLayout.EAST,
				this);

	}

	private JPanel initRightPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createEmptyBorder());

		panel.add(resourcePanel);

		layout.putConstraint(SpringLayout.NORTH, resourcePanel, 10,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, resourcePanel, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, resourcePanel, -10,
				SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, resourcePanel, -10,
				SpringLayout.SOUTH, panel);

		return panel;
	}

	private JPanel initLeftPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createEmptyBorder());

		filter = new JTextField(".*");
		SwingUtils.prepJTextField(filter);
		panel.add(filter);

		// Listen for changes in the text
		filterOkColor = filter.getBackground();
		filterBadColor = new Color(250, 210, 200);
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

		clearMenuItem = new JMenuItem("Clear");
		clearMenuItem.addActionListener(this);
		popup.add(clearMenuItem);

		removeRequestMenuItem = new JMenuItem("Remove Request");
		removeRequestMenuItem.addActionListener(this);
		popup.add(removeRequestMenuItem);

		replyMenuItem = new JMenuItem("Replay Request");
		replyMenuItem.addActionListener(this);
		popup.add(replyMenuItem);

		list = new JList<Resource>(model);
		list.setCellRenderer(new ResourceCellRenderer());
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
		panel.add(scroll);

		layout.putConstraint(SpringLayout.NORTH, filter, 10, SpringLayout.NORTH,
				panel);
		layout.putConstraint(SpringLayout.WEST, filter, 10, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.EAST, filter, -10, SpringLayout.EAST,
				panel);

		layout.putConstraint(SpringLayout.NORTH, scroll, 10,
				SpringLayout.SOUTH, filter);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST,
				panel);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10,
				SpringLayout.SOUTH, panel);

		return panel;
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
			Resource resource = (Resource) list.getSelectedValue();
			if (resource != null) {
				showResource(resource.getLoadedResource(), resource);
			}
		}
	}

	private void showResource(final LoadedResource lr, final Resource resource) {
		if (lr != null) {
			resourcePanel.setResource(lr);
		} else {
			resourcePanel.setResource(resource);
		}
	}

	public void setProxy(AjaxProxy ajaxProxy) {
		if (ajaxProxy != null) {
			ajaxProxy.addTracker(this);
			ajaxProxy.addRequestListener(this);
		}
	}

	@Override
	public void trackFile(LoadedResource res) {
		boolean show = toggleBtn.isSelected();

		if (show) {
			model.add(new Resource(res));
		}
	}

	private void resetFilter() {
		Pattern filterRegEx = null;
		if (!StringUtils.isBlank(filter.getText())) {
			try {
				filterRegEx = Pattern.compile(filter.getText());
				filter.setBackground(filterOkColor);
			} catch (PatternSyntaxException ex) {
				filterRegEx = null;
				filter.setBackground(filterBadColor);
			}
		}
		final Pattern filter = filterRegEx;
		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {
				model.setFilter(filter);
			}
		});
	}

	private void export() {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File folder = fc.getSelectedFile();
			String path = folder.getAbsolutePath();
			for (int i = 0; i < model.getSize(); i++) {
				Resource resource = (Resource) model.get(i);
				LoadedResource obj = resource.getLoadedResource();
				String fn = StringUtils.leftPad(String.valueOf(i), 8, "0");
				JsonObject json = new JsonObject();
				json.put("path", obj.getPath());
				json.put("input", obj.getInputAsText());
				json.put("output", obj.getOutputAsText());
				json.put("status", obj.getStatusCode());
				json.put("duration", obj.getDuration());
				json.put("method", obj.getMethod());
				JsonObject headers = new JsonObject();
				json.put("request headers", headers);
				if (obj.getRequestHeaders() != null) {
					for (Header hdr : obj.getRequestHeaders()) {
						headers.put(hdr.getName(), hdr.getValue());
					}
				}
				headers = new JsonObject();
				json.put("response headers", headers);
				if (obj.getResponseHeaders() != null) {
					for (Header hdr : obj.getResponseHeaders()) {
						headers.put(hdr.getName(), hdr.getValue());
					}
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
				final Resource resource = model.get(index);
				SwingUtils.executNonUi(new Runnable() {
					@Override
					public void run() {
						// TODO: maybe 127.0.0.1?
						// httpClient.replay("localhost", ajaxProxy.getPort(),
						// resource, null);

						RestClientFrame rest = new RestClientFrame();
						if (resource.getLoadedResource() != null) {
							rest.fromResource(resource.getLoadedResource());
						} else {
							rest.fromResource(resource);
						}
						rest.setVisible(true);
					}
				});
			}
		} else if (evt.getSource() == clearMenuItem) {
			clear();
		}
	}

	@Override
	public void newRequest(UUID id, String url, String method) {
		model.add(new Resource(id, url, method));
	}

	@Override
	public void startRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {
		model.startRequest(id, url, requestHeaders, data);
	}

	@Override
	public void requestComplete(UUID id, int status, String reason,
			long duration, Header[] responseHeaders, byte[] data) {
		model.requestComplete(id, status, reason, duration, responseHeaders,
				data);
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		model.error(id, message, e);
	}

	private void clear() {
		model.clear();
		showResource(null, null);
	}
}
