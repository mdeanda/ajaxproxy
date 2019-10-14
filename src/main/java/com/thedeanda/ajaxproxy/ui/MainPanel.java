package com.thedeanda.ajaxproxy.ui;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.config.ConfigLoader;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.Variable;
import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.ui.border.TopBorder;
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
	private JToggleButton serverToolbarButton;
	private JToggleButton requestToolbarButton;
	private JToggleButton loggerToolbarButton;
	private JToggleButton variablesToolbarButton;

	public MainPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(new BorderLayout());

		File dbFile = ConfigService.get().getResourceHistoryDb();
		resourceService = new ResourceService(CACHE_SIZE, dbFile);

		JComponent toolBar = initToolbar();
		add(toolBar, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(layout);
		contentPanel.setBorder(new TopBorder());
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

        variableController = new VariableController();
        variableController.addListener(this);
		variablePanel = new VariablesPanel(variableController);
		cardPanel.add(variablePanel, CARD_VARIABLES);

		loggerPanel = new LoggerPanel();
		cardPanel.add(loggerPanel, CARD_LOGGER);

		layout.putConstraint(SpringLayout.NORTH, cardPanel, 2, SpringLayout.NORTH, contentPanel);
		layout.putConstraint(SpringLayout.WEST, cardPanel, 0, SpringLayout.WEST, contentPanel);
		layout.putConstraint(SpringLayout.EAST, cardPanel, 0, SpringLayout.EAST, contentPanel);
		layout.putConstraint(SpringLayout.SOUTH, cardPanel, 0, SpringLayout.SOUTH, contentPanel);

		clearAll();
		//selectedCard(CARD_SERVER);
        //serverToolbarButton.setSelected(true);
	}

	private JComponent initToolbar() {
		/*
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		//*/

		JPanel toolBar = new JPanel();
		SpringLayout layout = new SpringLayout();
		toolBar.setLayout(layout);

		JToggleButton button = null;

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

		//*
		Dimension dim = button.getPreferredSize();
		dim.height += 10;
		dim.width = 600;
		toolBar.setPreferredSize(dim);
		 //*/

		layout.putConstraint(SpringLayout.WEST, serverToolbarButton, 2, SpringLayout.WEST, toolBar);
		layout.putConstraint(SpringLayout.EAST, serverToolbarButton, 140, SpringLayout.WEST, serverToolbarButton);
		layout.putConstraint(SpringLayout.NORTH, serverToolbarButton, 2, SpringLayout.NORTH, toolBar);
		layout.putConstraint(SpringLayout.SOUTH, serverToolbarButton, -2, SpringLayout.SOUTH, toolBar);

		layout.putConstraint(SpringLayout.WEST, requestToolbarButton, 2, SpringLayout.EAST, serverToolbarButton);
		layout.putConstraint(SpringLayout.EAST, requestToolbarButton, 150, SpringLayout.WEST, requestToolbarButton);
		layout.putConstraint(SpringLayout.NORTH, requestToolbarButton, 0, SpringLayout.NORTH, serverToolbarButton);
		layout.putConstraint(SpringLayout.SOUTH, requestToolbarButton, 0, SpringLayout.SOUTH, serverToolbarButton);

		layout.putConstraint(SpringLayout.WEST, variablesToolbarButton, 2, SpringLayout.EAST, requestToolbarButton);
		layout.putConstraint(SpringLayout.EAST, variablesToolbarButton, 110, SpringLayout.WEST, variablesToolbarButton);
		layout.putConstraint(SpringLayout.NORTH, variablesToolbarButton, 0, SpringLayout.NORTH, serverToolbarButton);
		layout.putConstraint(SpringLayout.SOUTH, variablesToolbarButton, 0, SpringLayout.SOUTH, serverToolbarButton);

		layout.putConstraint(SpringLayout.WEST, loggerToolbarButton, 2, SpringLayout.EAST, variablesToolbarButton);
		layout.putConstraint(SpringLayout.EAST, loggerToolbarButton, 90, SpringLayout.WEST, loggerToolbarButton);
		layout.putConstraint(SpringLayout.NORTH, loggerToolbarButton, 0, SpringLayout.NORTH, serverToolbarButton);
		layout.putConstraint(SpringLayout.SOUTH, loggerToolbarButton, 0, SpringLayout.SOUTH, serverToolbarButton);


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

	protected JToggleButton makeNavigationButton(String actionCommand,
										   String toolTipText,
										   String altText) {

		//Create and initialize the button.
		JToggleButton button = SwingUtils.newJToggleButton(altText);
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setText(altText);

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

	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(final File configFile) {
		this.configFile = configFile;
		InputStream is = null;
		try {
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
		//TODO: keep as Config object
		ConfigLoader cl = new ConfigLoader();
		Config co = cl.loadConfig(json, configDir);

		List<Variable> variables = co.getVariables();
		this.config = json;
		serverConfigPanel.setConfig(json, co);
        variableController.setConfig(variables);
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
			default:
		}
	}

	private void selectedCard(String cardName, JToggleButton selectedToolbarButton) {
		if (selectedToolbarButton != serverToolbarButton)
			serverToolbarButton.setSelected(false);
		if (selectedToolbarButton != requestToolbarButton)
			requestToolbarButton.setSelected(false);
		if (selectedToolbarButton != loggerToolbarButton)
			loggerToolbarButton.setSelected(false);
		if (selectedToolbarButton != variablesToolbarButton)
			variablesToolbarButton.setSelected(false);

		cardLayout.show(cardPanel, cardName);

		selectedToolbarButton.setSelected(true);
	}
}
