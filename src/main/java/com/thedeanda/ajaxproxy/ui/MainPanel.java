package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

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

public class MainPanel extends JPanel implements ProxyListener, SettingsChangedListener, NavListener, ActionListener {
	private static final Logger log = LoggerFactory.getLogger(MainPanel.class);
	private static final int CACHE_SIZE = 50;
	private final JToolBar toolBar;
	private static final String COMMAND_SERVER = "server";
	private static final String COMMAND_RESOURCE = "resource";
	private static final String COMMAND_LOGGER = "logger";

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
	private JToggleButton serverToolbarButton;
	private JToggleButton requestToolbarButton;
	private JToggleButton loggerToolbarButton;

	public MainPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		File dbFile = ConfigService.get().getResourceHistoryDb();
		resourceService = new ResourceService(CACHE_SIZE, dbFile);

		toolBar = initToolbar();
		add(toolBar);

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

		layout.putConstraint(SpringLayout.NORTH, toolBar, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, toolBar, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, toolBar, 0, SpringLayout.EAST, this);


		layout.putConstraint(SpringLayout.NORTH, navComponent, 0, SpringLayout.SOUTH, toolBar);
		layout.putConstraint(SpringLayout.SOUTH, navComponent, 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.WEST, navComponent, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, navComponent, 125, SpringLayout.WEST, navComponent);

		layout.putConstraint(SpringLayout.NORTH, cardPanel, 0, SpringLayout.SOUTH, toolBar);
		layout.putConstraint(SpringLayout.WEST, cardPanel, 0, SpringLayout.EAST, navComponent);
		layout.putConstraint(SpringLayout.EAST, cardPanel, 0, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, cardPanel, 0, SpringLayout.SOUTH, this);

		clearAll();
	}

	private JToolBar initToolbar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		JToggleButton button = null;

		button = makeNavigationButton("a", COMMAND_SERVER,
				"Configure server and proxy paths",
				"Server Config");
		toolBar.add(button);
		//prepPopup(button);
		serverToolbarButton = button;

		button = makeNavigationButton("b", COMMAND_RESOURCE,
				"View requested handled by Ajax Proxy",
				"Request Viewer");
		toolBar.add(button);
		requestToolbarButton = button;

		button = makeNavigationButton("c", COMMAND_LOGGER,
				"View log messages posted by a remote application",
				"Logger");
		toolBar.add(button);
		loggerToolbarButton = button;

		return toolBar;
	}

	private void prepPopup(JComponent btn) {
		JPopupMenu pop = new JPopupMenu("test");
		JMenuItem cutMenuItem = new JMenuItem("Cut");
		cutMenuItem.setActionCommand("Cut");
		pop.add(cutMenuItem);

		btn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				pop.show(MainPanel.this, 5, btn.getHeight()+2);
			}
		});

	}

	protected JToggleButton makeNavigationButton(String imageName,
										   String actionCommand,
										   String toolTipText,
										   String altText) {
		//Look for the image.
		String imgLocation = "images/"
				+ imageName
				+ ".gif";
		URL imageURL = MainPanel.class.getResource(imgLocation);

		//Create and initialize the button.
		JToggleButton button = new JToggleButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) {                      //image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else {                                     //no image found
			button.setText(altText);
			log.warn("Resource not found: " + imgLocation);
		}

		return button;
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
			proxy = new AjaxProxy(json, workingDir, resourceService);
			proxy.addProxyListener(this);
			new Thread(proxy).start();
			generalPanel.setProxy(proxy);
			loggerPanel.setProxy(proxy);
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
			showCard(CARD_LOGGER);
			break;
		case RequestViewer:
			showCard(CARD_RESOURCE_VIEWER);
			break;
		case Server:
			showCard(CARD_SERVER);
			break;
		case Stop:
			stop();
			break;
		case Start:
			start();
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
			case COMMAND_SERVER:
				showCard(CARD_SERVER);

				break;
			case COMMAND_RESOURCE:
				showCard(CARD_RESOURCE_VIEWER);

				break;
			case COMMAND_LOGGER:
				showCard(CARD_LOGGER);

				break;
			default:
		}
	}

	private void showCard(String cardName) {
		serverToolbarButton.setSelected(false);
		requestToolbarButton.setSelected(false);
		loggerToolbarButton.setSelected(false);

		cardLayout.show(cardPanel, cardName);

		switch(cardName) {
			case CARD_SERVER:
				serverToolbarButton.setSelected(true);
				break;
			case CARD_RESOURCE_VIEWER:
				requestToolbarButton.setSelected(true);
				break;
			case CARD_LOGGER:
				loggerToolbarButton.setSelected(true);
				break;
			default:
		}
	}
}
