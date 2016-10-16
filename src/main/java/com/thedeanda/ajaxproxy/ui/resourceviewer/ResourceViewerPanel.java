package com.thedeanda.ajaxproxy.ui.resourceviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
import com.thedeanda.ajaxproxy.ui.model.Resource;
import com.thedeanda.ajaxproxy.ui.model.ResourceListModel;
import com.thedeanda.ajaxproxy.ui.resourceviewer.list.ResourceListPanel;
import com.thedeanda.javajson.JsonObject;

/** tracks files that get loaded */
public class ResourceViewerPanel extends JPanel implements AccessTracker, RequestListener {
	private static final Logger log = LoggerFactory.getLogger(ResourceViewerPanel.class);
	private static final long serialVersionUID = 1L;
	private JButton clearBtn;
	private JButton exportBtn;
	private JCheckBox toggleBtn;
	private ResourcePanel resourcePanel;
	private ResourceService resourceService;
	private ResourceListModel model;
	private ResourceListPanel resourceListPanel;

	public ResourceViewerPanel(ResourceService resourceService) {
		log.debug("new viewer");
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		this.resourceService = resourceService;
		this.model = new ResourceListModel(resourceService);

		resourcePanel = new ResourcePanel(resourceService, false);
		clearBtn = new JButton("Clear");
		exportBtn = new JButton("Export");
		toggleBtn = new JCheckBox("Monitor Resources");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resourceListPanel.clear();
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

		topPanelLayout.putConstraint(SpringLayout.NORTH, clearBtn, 20, SpringLayout.NORTH, topPanel);
		topPanelLayout.putConstraint(SpringLayout.WEST, clearBtn, 10, SpringLayout.WEST, topPanel);
		topPanelLayout.putConstraint(SpringLayout.NORTH, exportBtn, 0, SpringLayout.NORTH, clearBtn);
		topPanelLayout.putConstraint(SpringLayout.WEST, exportBtn, 10, SpringLayout.EAST, clearBtn);
		topPanelLayout.putConstraint(SpringLayout.NORTH, toggleBtn, 20, SpringLayout.NORTH, topPanel);
		topPanelLayout.putConstraint(SpringLayout.EAST, toggleBtn, -10, SpringLayout.EAST, topPanel);

		layout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, topPanel, 60, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, split, 0, SpringLayout.SOUTH, topPanel);
		layout.putConstraint(SpringLayout.SOUTH, split, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, split, 0, SpringLayout.EAST, this);

	}

	private JPanel initRightPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		panel.setBorder(BorderFactory.createEmptyBorder());

		panel.add(resourcePanel);

		layout.putConstraint(SpringLayout.NORTH, resourcePanel, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, resourcePanel, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, resourcePanel, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, resourcePanel, -10, SpringLayout.SOUTH, panel);

		return panel;
	}

	private JPanel initLeftPanel() {
		resourceListPanel = new ResourceListPanel(this, resourceService, model);
		return resourceListPanel;
	}

	protected void showResource(Resource resource) {
		resourcePanel.setResource(resource);
	}

	public void setProxy(AjaxProxy ajaxProxy) {
		if (ajaxProxy != null) {
			// ajaxProxy.addTracker(this);
			ajaxProxy.addRequestListener(this);
		}
	}

	/*
	 * @Override public void trackFile(LoadedResource res) { boolean show =
	 * toggleBtn.isSelected();
	 * 
	 * if (show) { model.add(new Resource(res)); } }
	 */

	private void export() {
		/*
		 * //TODO: move this off swing thread final JFileChooser fc = new
		 * JFileChooser();
		 * fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); int returnVal
		 * = fc.showSaveDialog(this); if (returnVal ==
		 * JFileChooser.APPROVE_OPTION) { File folder = fc.getSelectedFile();
		 * String path = folder.getAbsolutePath(); for (int i = 0; i <
		 * model.getSize(); i++) { Resource resource = (Resource) model.get(i);
		 * LoadedResource obj = resource.getLoadedResource(); String fn =
		 * StringUtils.leftPad(String.valueOf(i), 8, "0"); JsonObject json = new
		 * JsonObject(); json.put("path", obj.getPath()); json.put("input",
		 * obj.getInputAsText()); json.put("output", obj.getOutputAsText());
		 * json.put("status", obj.getStatusCode()); json.put("duration",
		 * obj.getDuration()); json.put("method", obj.getMethod()); JsonObject
		 * headers = new JsonObject(); json.put("request headers", headers); if
		 * (obj.getRequestHeaders() != null) { for (Header hdr :
		 * obj.getRequestHeaders()) { headers.put(hdr.getName(),
		 * hdr.getValue()); } } headers = new JsonObject(); json.put(
		 * "response headers", headers); if (obj.getResponseHeaders() != null) {
		 * for (Header hdr : obj.getResponseHeaders()) {
		 * headers.put(hdr.getName(), hdr.getValue()); } }
		 * 
		 * Writer writer = null; try { writer = new OutputStreamWriter(new
		 * FileOutputStream( new File(path + File.separator + fn + ".txt")),
		 * "UTF-8"); writer.write(json.toString(4)); } catch (Exception e) {
		 * e.printStackTrace(); } finally { if (writer != null) { try {
		 * writer.close(); } catch (IOException e) { e.printStackTrace(); } } }
		 * 
		 * } } //
		 */
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
	public void newRequest(UUID id, String url, String method) {
		boolean enableMonitor = toggleBtn.isSelected();
		if (enableMonitor) {
			final Resource resource = new Resource(id, url, method);
			model.add(resource);
		}
	}

	@Override
	public void startRequest(final UUID id, final URL url, final Header[] requestHeaders, final byte[] data) {
		boolean enableMonitor = toggleBtn.isSelected();
		if (enableMonitor) {
			model.startRequest(id, url, requestHeaders, data);
		}
	}

	@Override
	public void requestComplete(final UUID id, final int status, final String reason, final long duration,
			final Header[] responseHeaders, final byte[] data) {
		boolean enableMonitor = toggleBtn.isSelected();
		if (enableMonitor) {
			model.requestComplete(id, status, reason, duration, responseHeaders, data);
		}
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		model.error(id, message, e);
	}
}
