package com.thedeanda.ajaxproxy.ui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.ui.logger.LoggerPanel;
import com.thedeanda.ajaxproxy.ui.merge.MergePanel;
import com.thedeanda.ajaxproxy.ui.merge.MergeTableModel;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyPanel;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyTableModel;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ResourceViewerPanel;
import com.thedeanda.ajaxproxy.ui.tamper.TamperPanel;
import com.thedeanda.ajaxproxy.ui.tracker.FileTrackerPanel;
import com.thedeanda.ajaxproxy.ui.update.UpdateCheckWorker;
import com.thedeanda.ajaxproxy.ui.variable.VariablesPanel;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;

public class MainPanel extends JPanel implements ProxyListener, SettingsChangedListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainPanel.class);
	private static final int CACHE_SIZE = 50;

	private JButton startStopBtn;
	private boolean started = false;
	private AjaxProxy proxy = null;
	private ProxyTableModel proxyModel;
	private MergeTableModel mergeModel;
	private File configFile;
	private JsonObject config;
	private FileTrackerPanel trackerPanel;
	private JTabbedPane tabs;
	private GeneralPanel generalPanel;
	private TamperPanel tamperPanel;

	private static final String START = "Start";
	private static final String STOP = "Stop";

	private List<ProxyListener> listeners = new ArrayList<ProxyListener>();
	private ResourceViewerPanel resourceViewerPanel;
	private ResourceService resourceService;
	private JButton releasesButton;
	private VariablesPanel variablePanel;
	private LoggerPanel loggerPanel;

	public MainPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		File dbFile = ConfigService.get().getResourceHistoryDb();
		resourceService = new ResourceService(CACHE_SIZE, dbFile);

		startStopBtn = new JButton(START);
		startStopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (started)
					stop();
				else
					start();
			}
		});

		this.tabs = new JTabbedPane();
		add(tabs);

		generalPanel = new GeneralPanel(this);
		tabs.add("General", generalPanel);

		proxyModel = new ProxyTableModel();
		tabs.add("Proxy", new ProxyPanel(this, proxyModel));

		mergeModel = new MergeTableModel();
		tabs.add("Merge", new MergePanel(this, mergeModel));

		// TODO: move proxy to its own panel so code is easier to maintain
		variablePanel = new VariablesPanel(this);
		tabs.add("Variables", variablePanel);

		tamperPanel = new TamperPanel();
		// tabs.add("Tamper", tamperPanel);

		trackerPanel = new FileTrackerPanel();
		// tabs.add("Tracker", trackerPanel);

		resourceViewerPanel = new ResourceViewerPanel(resourceService);
		tabs.add("Resource Viewer", resourceViewerPanel);

		loggerPanel = new LoggerPanel();
		tabs.add("Logger", loggerPanel);
		
		add(startStopBtn);
		prepareReleasesButton();
		add(releasesButton);

		layout.putConstraint(SpringLayout.SOUTH, startStopBtn, -10, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.EAST, startStopBtn, -10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, tabs, 15, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, tabs, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, tabs, -10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, tabs, -10, SpringLayout.NORTH, startStopBtn);

		layout.putConstraint(SpringLayout.SOUTH, releasesButton, 0, SpringLayout.SOUTH, startStopBtn);
		layout.putConstraint(SpringLayout.WEST, releasesButton, 10, SpringLayout.WEST, this);

		clearAll();

	}

	private void prepareReleasesButton() {
		releasesButton = new JButton("Update Available");
		releasesButton.addActionListener(this);
		releasesButton.setVisible(false);

		new UpdateCheckWorker() {
			@Override
			protected void done() {
				Boolean updateAvailable = null;
				try {
					updateAvailable = get();
				} catch (InterruptedException | ExecutionException e) {
				}
				if (updateAvailable == Boolean.TRUE) {
					releasesButton.setVisible(true);
					String message = getMessage();
					if (!StringUtils.isBlank(message)) {
						releasesButton.setToolTipText(message);
					}
				}
			}
		}.execute();
	}

	public void addProxyListener(ProxyListener listener) {
		listeners.add(listener);

		if (started)
			listener.started();
		else
			listener.stopped();
	}

	private void fireProxyStarted() {
		for (ProxyListener l : listeners) {
			l.started();
		}
	}

	private void fireProxyStopped() {
		for (ProxyListener l : listeners) {
			l.stopped();
		}
	}

	/**
	 * updates the config from the ui data
	 * 
	 * @return the json object representing the config
	 */
	public JsonObject getConfig() {
		JsonObject json = config;
		json.put("proxy", proxyModel.getConfig(generalPanel.getCacheTime()));
		json.put("merge", mergeModel.getConfig());
		json.put("variables", variablePanel.getConfig());
		json.put("tracker", trackerPanel.getConfig());
		json.put("resource", resourceViewerPanel.getConfig());
		json.put("tamper", tamperPanel.getConfig());

		generalPanel.updateConfig(json);
		loggerPanel.updateConfig(json);
		
		log.trace(json.toString(2));
		return json;
	}

	/**
	 * ui settings not stored in the current config file
	 * 
	 * @return
	 */
	public JsonObject getSettings() {
		JsonObject ret = new JsonObject();
		ret.put("currentTab", tabs.getSelectedIndex());
		return ret;
	}

	/**
	 * load ui settings
	 * 
	 * @param json
	 */
	public void setSettings(JsonObject json) {
		if (json == null)
			return;

		int tab = json.getInt("currentTab");
		if (tabs.getTabCount() > tab) {
			tabs.setSelectedIndex(tab);
		}
	}

	public void start() {
		if (started)
			return;

		try {
			startStopBtn.setText(STOP);
			JsonObject json = JsonObject.parse(getConfig().toString());
			File workingDir = configFile.getParentFile();
			if (workingDir == null)
				workingDir = new File(".");
			proxy = new AjaxProxy(json, workingDir);
			proxy.addProxyListener(this);
			new Thread(proxy).start();
			proxy.addRequestListener(resourceService);
			generalPanel.setProxy(proxy);
			trackerPanel.setProxy(proxy);
			loggerPanel.setProxy(proxy);
			resourceViewerPanel.setProxy(proxy);
			started = true;
			fireProxyStarted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			failed();
		}
	}

	public void clearAll() {
		stop();
		configFile = new File("");
		config = new JsonObject();

		generalPanel.setPort(0);
		generalPanel.setResourceBase("");
		generalPanel.setShowIndex(false);
		proxyModel.clear();
		mergeModel.clear();
		variablePanel.clear();
	}

	public void stop() {
		try {
			if (proxy != null) {
				log.info("stopping server");
				AjaxProxy p = proxy;
				proxy = null;
				p.stop();
				generalPanel.setProxy(null);
				trackerPanel.setProxy(null);
				resourceViewerPanel.setProxy(null);
			}
		} finally {
			proxy = null;
			started = false;
			startStopBtn.setText(START);
			fireProxyStopped();
		}
	}

	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(final File configFile) {
		this.configFile = configFile;
		InputStream is = null;
		try {
			is = new FileInputStream(configFile);
			setConfig(JsonObject.parse(is));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(MainPanel.this, "Error reading file");
			clearAll();
		} catch (JsonException e) {
			log.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(MainPanel.this, "Error parsing file");
			clearAll();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	public void setConfig(JsonObject json) {
		this.config = json;
		proxyModel.setConfig(config.getJsonArray("proxy"));
		mergeModel.setConfig(config.getJsonArray("merge"));
		variablePanel.setConfig(config.getJsonObject("variables"));
		generalPanel.setConfig(config);
		trackerPanel.setConfig(json.getJsonObject("tracker"));
		resourceViewerPanel.setConfig(json.getJsonObject("resource"));
		tamperPanel.setConfig(json.getJsonObject("tamper"));
	}

	@Override
	public void started() {

	}

	@Override
	public void stopped() {
		this.stop();
	}

	@Override
	public void failed() {
		log.error("failed, so calling stop");
		this.stop();
	}

	@Override
	public void restartRequired() {
		if (started) {
			stop();
			start();
		}
	}

	@Override
	public void settingsChanged() {
		log.debug("settings changed, possibly track to warn of unsaved changes during close");
	}

	public void addVariables(Map<String, String> vars) {
		variablePanel.setVariables(vars);
	}

	private void openReleasesPage() {
		try {
			URI url = new URI(UpdateCheckWorker.RELEASE_URL);
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == releasesButton) {
			openReleasesPage();
		}
	}
}
