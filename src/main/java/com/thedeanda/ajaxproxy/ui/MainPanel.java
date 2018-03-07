package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.ui.border.RightBorder;
import com.thedeanda.ajaxproxy.ui.logger.LoggerPanel;
import com.thedeanda.ajaxproxy.ui.main.nav.MainNavPanel;
import com.thedeanda.ajaxproxy.ui.main.nav.NavItem;
import com.thedeanda.ajaxproxy.ui.main.nav.NavListener;
import com.thedeanda.ajaxproxy.ui.merge.MergePanel;
import com.thedeanda.ajaxproxy.ui.merge.MergeTableModel;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyPanel;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyTableModel;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ResourceViewerPanel;
import com.thedeanda.ajaxproxy.ui.tamper.TamperPanel;
import com.thedeanda.ajaxproxy.ui.update.UpdateCheckWorker;
import com.thedeanda.ajaxproxy.ui.variable.VariablesPanel;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;

public class MainPanel extends JPanel implements ProxyListener, SettingsChangedListener, NavListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainPanel.class);
	private static final int CACHE_SIZE = 50;

	private boolean started = false;
	private AjaxProxy proxy = null;
	private ProxyTableModel proxyModel;
	private MergeTableModel mergeModel;
	private File configFile;
	private JsonObject config;
	// private JTabbedPane tabs;
	private GeneralPanel generalPanel;
	private TamperPanel tamperPanel;

	private static final String START = "Start";
	private static final String STOP = "Stop";

	private List<ProxyListener> listeners = new ArrayList<ProxyListener>();
	private ResourceViewerPanel resourceViewerPanel;
	private ResourceService resourceService;
	private VariablesPanel variablePanel;
	private LoggerPanel loggerPanel;

	private JPanel cardPanel;
	private CardLayout cardLayout;
	private MainNavPanel navPanel;
	private static final String CARD_SERVER = "card_server";
	private static final String CARD_RESOURCE_VIEWER = "card_resource_viewer";
	private static final String CARD_LOGGER = "card_logger";

	public MainPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		File dbFile = ConfigService.get().getResourceHistoryDb();
		resourceService = new ResourceService(CACHE_SIZE, dbFile);

		cardPanel = new JPanel();
		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);

		add(cardPanel);

		JTabbedPane tabs = new JTabbedPane();
		JPanel tabsPanel = new JPanel();
		tabsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabsPanel.setLayout(new BorderLayout());
		tabsPanel.add(tabs, BorderLayout.CENTER);
		cardPanel.add(tabsPanel, CARD_SERVER);

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

		resourceViewerPanel = new ResourceViewerPanel(resourceService);
		// tabs.add("Resource Viewer", resourceViewerPanel);
		cardPanel.add(resourceViewerPanel, CARD_RESOURCE_VIEWER);

		loggerPanel = new LoggerPanel();
		// tabs.add("Logger", loggerPanel);
		cardPanel.add(loggerPanel, CARD_LOGGER);

		navPanel = new MainNavPanel();
		JScrollPane navComponent = new JScrollPane(navPanel);
		add(navComponent);
		navComponent.setBorder(null);
		navPanel.addNavListener(this);
		navPanel.setBorder(new RightBorder());

		layout.putConstraint(SpringLayout.NORTH, navComponent, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, navComponent, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, navComponent, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, navComponent, 100, SpringLayout.WEST, navComponent);

		layout.putConstraint(SpringLayout.NORTH, cardPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, cardPanel, 0, SpringLayout.EAST, navComponent);
		layout.putConstraint(SpringLayout.EAST, cardPanel, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, cardPanel, 0, SpringLayout.SOUTH, this);

		clearAll();
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
	}

	public void start() {
		if (started)
			return;

		try {
			JsonObject json = JsonObject.parse(getConfig().toString());
			File workingDir = configFile.getParentFile();
			if (workingDir == null)
				workingDir = new File(".");
			proxy = new AjaxProxy(json, workingDir);
			proxy.addProxyListener(this);
			new Thread(proxy).start();
			proxy.addRequestListener(resourceService);
			generalPanel.setProxy(proxy);
			loggerPanel.setProxy(proxy);
			resourceViewerPanel.setProxy(proxy);
			started = true;
			navPanel.selectNavItem(NavItem.Start, 0);
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
				resourceViewerPanel.setProxy(null);
			}
		} finally {
			proxy = null;
			started = false;
			navPanel.selectNavItem(NavItem.Stop, 0);
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
	public void navEvent(NavItem navItem, int index) {
		switch (navItem) {
		case Logger:
			cardLayout.show(cardPanel, CARD_LOGGER);
			break;
		case RequestViewer:
			cardLayout.show(cardPanel, CARD_RESOURCE_VIEWER);
			break;
		case Server:
			cardLayout.show(cardPanel, CARD_SERVER);
			break;
		case Stop:
			stop();
			break;
		case Start:
			start();
			break;
		}
	}
}
