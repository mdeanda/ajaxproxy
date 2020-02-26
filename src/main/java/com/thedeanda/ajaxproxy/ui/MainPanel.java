package com.thedeanda.ajaxproxy.ui;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.config.ConfigFileService;
import com.thedeanda.ajaxproxy.config.ConfigLoader;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.Variable;
import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.ui.border.TopBorder;
import com.thedeanda.ajaxproxy.ui.json.JsonViewer;
import com.thedeanda.ajaxproxy.ui.logger.LoggerPanel;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ResourceViewerPanel;
import com.thedeanda.ajaxproxy.ui.serverconfig.ServerConfigPanel;
import com.thedeanda.ajaxproxy.ui.util.SwingUtils;
import com.thedeanda.ajaxproxy.ui.variable.controller.VariableController;
import com.thedeanda.ajaxproxy.ui.variable.VariablesPanel;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPanel extends JPanel implements ProxyListener, SettingsChangedListener, ActionListener {
	private static final Logger log = LoggerFactory.getLogger(MainPanel.class);
	private static final int CACHE_SIZE = 50;
	//private final JToolBar toolBar;
	private final ServerConfigPanel serverConfigPanel;

	private boolean started = false;
	private AjaxProxyServer proxy = null;
	private File configFile;
	private JsonObject config;

	private List<ProxyListener> listeners = new ArrayList<ProxyListener>();
	private ResourceViewerPanel resourceViewerPanel;
	private ResourceService resourceService;
	private LoggerPanel loggerPanel;
	private VariablesPanel variablePanel;
	private VariableController variableController;

	private JPanel cardPanel;
	private CardLayout cardLayout;
	private static final String CARD_SERVER = "card_server";
	private static final String CARD_RESOURCE_VIEWER = "card_resource_viewer";
	private static final String CARD_VARIABLES = "card_variables";
	private static final String CARD_LOGGER = "card_logger";
	private static final String CARD_JSON = "json_viewer";

	private final ConfigFileService configFileService;

	private JButton serverToolbarButton;
	private JButton requestToolbarButton;
	private JButton loggerToolbarButton;
	private JButton variablesToolbarButton;
	private JButton jsonToolbarButton;

	public MainPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(new BorderLayout());

		configFileService = new ConfigFileService();

		File dbFile = ConfigService.get().getResourceHistoryDb();
		resourceService = new ResourceService(CACHE_SIZE, dbFile);

		JComponent toolBar = initToolbar();
		add(toolBar, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(layout);
		//contentPanel.setBorder(new TopBorder());
		add(contentPanel, BorderLayout.CENTER);

		cardPanel = new JPanel();
		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);

		contentPanel.add(cardPanel);

		serverConfigPanel = new ServerConfigPanel(this, this);
		cardPanel.add(serverConfigPanel, CARD_SERVER);
		addProxyListener(serverConfigPanel);

		resourceViewerPanel = new ResourceViewerPanel(resourceService);
		cardPanel.add(resourceViewerPanel, CARD_RESOURCE_VIEWER);

        variableController = new VariableController(configFileService);
        variableController.addListener(this);
		variablePanel = new VariablesPanel(variableController);
		cardPanel.add(variablePanel, CARD_VARIABLES);

		loggerPanel = new LoggerPanel();
		cardPanel.add(loggerPanel, CARD_LOGGER);

		JsonViewer jsonPanel = new JsonViewer();
		cardPanel.add(jsonPanel, CARD_JSON);

		layout.putConstraint(SpringLayout.NORTH, cardPanel, 2, SpringLayout.NORTH, contentPanel);
		layout.putConstraint(SpringLayout.WEST, cardPanel, 0, SpringLayout.WEST, contentPanel);
		layout.putConstraint(SpringLayout.EAST, cardPanel, 0, SpringLayout.EAST, contentPanel);
		layout.putConstraint(SpringLayout.SOUTH, cardPanel, 0, SpringLayout.SOUTH, contentPanel);

		clearAll();
		//selectedCard(CARD_SERVER);
        //serverToolbarButton.setSelected(true);
	}

	private JComponent initToolbar() {
		//*
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		//toolBar.setRollover(true);
		//*/

		JButton button = null;

		button = makeNavigationButton(CARD_SERVER,
				"Configure server and proxy paths",
				"Server Config");
		toolBar.add(button);
		//prepPopup(button);
		serverToolbarButton = button;

		button = makeNavigationButton(CARD_RESOURCE_VIEWER,
				"View requested handled by Ajax Proxy",
				"Request Viewer");
		toolBar.add(button);
		requestToolbarButton = button;


		button = makeNavigationButton(CARD_VARIABLES,
				"View variables",
				"Variables");
		toolBar.add(button);
		variablesToolbarButton = button;

		button = makeNavigationButton(CARD_LOGGER,
				"View log messages posted by a remote application",
				"Logger");
		toolBar.add(button);
		loggerToolbarButton = button;

		button = makeNavigationButton(CARD_JSON,
				"Json/Xml Formatter",
				"Text Formatter");
		toolBar.add(button);
		jsonToolbarButton = button;

		//*
		Dimension dim = button.getPreferredSize();
		dim.height += 10;
		dim.width = 100;
		toolBar.setPreferredSize(dim);
		 //*/

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

	protected JButton makeNavigationButton(String actionCommand,
										   String toolTipText,
										   String altText) {

		//Create and initialize the button.
		Icon icon = MetalIconFactory.getFileChooserDetailViewIcon();
		JButton button = new JButton(altText, icon);
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setIcon(icon);
		//button.setText(altText);
		//Insets insets = new Insets(4, 0, 4, 14);
		//button.setMargin(insets);
		//button.getInsets(insets);
		button.setMargin(new Insets(2, 0, 2, 10));

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

		serverConfigPanel.updateConfig(json);
		loggerPanel.updateConfig(json);
		JsonObject vars = variableController.getConfig();
		json.put("variables", vars);

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
			proxy = new AjaxProxyServer(json, workingDir, resourceService);
			proxy.addProxyListener(this);
			new Thread(proxy).start();
			serverConfigPanel.setProxy(proxy);
			loggerPanel.setProxy(proxy);
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

		serverConfigPanel.clearAll();
		variableController.clear();
	}

	public void stop() {
		try {
			if (proxy != null) {
				log.info("stopping server");
				AjaxProxyServer p = proxy;
				proxy = null;
				p.stop();
				serverConfigPanel.setProxy(null);
			}
		} finally {
			proxy = null;
			started = false;
			fireProxyStopped();
		}
	}

	@Deprecated
	private File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(final File configFile) {
		this.configFile = configFile;
		InputStream is = null;
		try {
			configFileService.loadConfigFile(configFile);
			is = new FileInputStream(configFile);
			setConfig(JsonObject.parse(is), configFile.getParentFile());
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

	// NOTE: perhaps pass in original file instead and manage it all here
	private void setConfig(JsonObject json, File configDir) {
		//xxx
		//TODO: keep as Config object
		ConfigLoader cl = new ConfigLoader();
		Config co = cl.loadConfig(json, configDir);

		List<Variable> variables = co.getVariables();
		this.config = json;
		serverConfigPanel.setConfig(json, co);
        //variableController.setConfig(variables);
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

	/**
	 * adds variables from command line
	 * @param vars
	 */
	public void addVariables(Map<String, String> vars) {
		serverConfigPanel.addVariables(vars);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		showCard(e.getActionCommand());
	}

	private void showCard(String cardName) {

		switch(cardName) {
			case CARD_SERVER:
				selectedCard(CARD_SERVER, serverToolbarButton);
				break;
			case CARD_RESOURCE_VIEWER:
				selectedCard(CARD_RESOURCE_VIEWER, requestToolbarButton);
				break;
			case CARD_LOGGER:
				selectedCard(CARD_LOGGER, loggerToolbarButton);
				break;
			case CARD_VARIABLES:
				selectedCard(CARD_VARIABLES, variablesToolbarButton);
				break;
			case CARD_JSON:
				selectedCard(CARD_JSON, jsonToolbarButton);
				break;
			default:
		}
	}

	private void selectedCard(String cardName, JButton selectedToolbarButton) {
		/*
		if (selectedToolbarButton != serverToolbarButton)
			serverToolbarButton.setSelected(false);
		if (selectedToolbarButton != requestToolbarButton)
			requestToolbarButton.setSelected(false);
		if (selectedToolbarButton != loggerToolbarButton)
			loggerToolbarButton.setSelected(false);
		if (selectedToolbarButton != variablesToolbarButton)
			variablesToolbarButton.setSelected(false);
		if (selectedToolbarButton != jsonToolbarButton)
			jsonToolbarButton.setSelected(false);

		 */

		cardLayout.show(cardPanel, cardName);

		//selectedToolbarButton.setSelected(true);
	}
}
