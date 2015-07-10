package com.thedeanda.ajaxproxy.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.ListenerSupportedSimpleLoggerFactory;
import org.slf4j.impl.LogListener;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyPanel;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyTableModel;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;

public class MainPanel extends JPanel implements ProxyListener, LogListener,
		SettingsChangedListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainPanel.class);
	private JButton btn;
	private boolean started = false;
	private AjaxProxy proxy = null;
	private ProxyTableModel proxyModel;
	private MergeTableModel mergeModel;
	private VariableTableModel variableModel;
	private File configFile;
	private JsonObject config;
	private JTextArea logBox;
	private OptionsPanel optionsPanel;
	private FileTrackerPanel trackerPanel;
	private JTabbedPane tabs;
	private JScrollPane logBoxScrollPane;
	private GeneralPanel generalPanel;

	private static final String START = "Start";
	private static final String STOP = "Stop";

	private List<ProxyListener> listeners = new ArrayList<ProxyListener>();
	private ResourceViewerPanel resourceViewerPanel;
	private JTextArea logTextArea;
	private JButton restartButton;

	public MainPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		ListenerSupportedSimpleLoggerFactory.addListener(this);

		btn = new JButton(START);
		btn.addActionListener(new ActionListener() {
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
		variableModel = new VariableTableModel();
		tabs.add("Variables", new VariablesPanel(this, variableModel));

		optionsPanel = new OptionsPanel();
		tabs.add("Options", optionsPanel);

		trackerPanel = new FileTrackerPanel();
		tabs.add("Tracker", trackerPanel);

		resourceViewerPanel = new ResourceViewerPanel();
		tabs.add("Resource Viewer", resourceViewerPanel);

		logTextArea = new JTextArea();
		logTextArea.setWrapStyleWord(true);
		logTextArea.setLineWrap(true);
		this.logBox = logTextArea;
		Font font = logTextArea.getFont();
		logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, font
				.getSize()));
		// ta.setEditable(false);
		this.logBoxScrollPane = new JScrollPane(logTextArea);
		tabs.add("Log", logBoxScrollPane);
		// LF5SwingUtils.makeVerticalScrollBarTrack(logBoxScrollPane);

		add(btn);
		restartButton = new JButton("Restart Required");
		add(restartButton);
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (started) {
					stop();
					start();
				}
			}
		});

		layout.putConstraint(SpringLayout.SOUTH, btn, -10, SpringLayout.SOUTH,
				this);
		layout.putConstraint(SpringLayout.EAST, btn, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.NORTH, tabs, 15, SpringLayout.NORTH,
				this);
		layout.putConstraint(SpringLayout.WEST, tabs, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, tabs, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.SOUTH, tabs, -10, SpringLayout.NORTH,
				btn);

		layout.putConstraint(SpringLayout.SOUTH, restartButton, 0,
				SpringLayout.SOUTH, btn);
		layout.putConstraint(SpringLayout.EAST, restartButton, -10,
				SpringLayout.WEST, btn);

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

	/** updates the config from the ui data */
	public JsonObject getConfig() {
		JsonObject json = config;
		json.put("port", generalPanel.getPort());
		json.put("resourceBase", generalPanel.getResourceBase());
		json.put(AjaxProxy.SHOW_INDEX, generalPanel.isShowIndex());
		json.put(AjaxProxy.NEW_PROXY, generalPanel.isNewProxy());
		json.put("proxy", proxyModel.getConfig());
		json.put("merge", mergeModel.getConfig());
		json.put("variables", variableModel.getConfig());
		json.put("tracker", trackerPanel.getConfig());
		json.put("resource", resourceViewerPanel.getConfig());
		json.put("options", optionsPanel.getConfig());

		log.info(json.toString(2));
		return json;
	}

	// TODO: figure out why this returns nothing
	public JsonObject getSettings() {
		JsonObject ret = new JsonObject();
		// ret.put("port", port.getText());
		return ret;
	}

	// TODO: figure out why this does nothing
	public void setSettings(JsonObject json) {
		if (json == null)
			return;

		// port.setText(json.getString("port"));
	}

	public void start() {
		if (started)
			return;

		logBox.setText("");
		try {
			btn.setText(STOP);
			JsonObject json = JsonObject.parse(getConfig().toString());
			File workingDir = configFile.getParentFile();
			if (workingDir == null)
				workingDir = new File(".");
			proxy = new AjaxProxy(json, workingDir);
			proxy.addProxyListener(this);
			new Thread(proxy).start();
			optionsPanel.setProxy(proxy);
			trackerPanel.setProxy(proxy);
			resourceViewerPanel.setProxy(proxy);
			started = true;
			fireProxyStarted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			failed();
			this.tabs.setSelectedComponent(logBoxScrollPane);
		}
	}

	public void clearAll() {
		stop();
		configFile = new File("");
		config = new JsonObject();

		generalPanel.setPort(0);
		generalPanel.setResourceBase("");
		generalPanel.setShowIndex(false);
		generalPanel.setNewProxy(false);
		proxyModel.clear();
		mergeModel.clear();
		variableModel.clear();
	}

	public void stop() {
		restartButton.setVisible(false);
		try {
			if (proxy != null) {
				log.info("stopping server");
				AjaxProxy p = proxy;
				proxy = null;
				p.stop();
				optionsPanel.setProxy(null);
				trackerPanel.setProxy(null);
				resourceViewerPanel.setProxy(null);
			}
		} finally {
			proxy = null;
			started = false;
			btn.setText(START);
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
		variableModel.setConfig(config.getJsonObject("variables"));
		generalPanel.setPort(config.getInt("port"));
		generalPanel.setResourceBase(config.getString("resourceBase"));
		generalPanel.setShowIndex(config.getBoolean(AjaxProxy.SHOW_INDEX));
		generalPanel.setNewProxy(config.getBoolean(AjaxProxy.NEW_PROXY));
		trackerPanel.setConfig(json.getJsonObject("tracker"));
		resourceViewerPanel.setConfig(json.getJsonObject("resource"));
		optionsPanel.setConfig(json.getJsonObject("options"));
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
	public void write(String msg) {
		logTextArea.append(msg);
	}

	@Override
	public void restartRequired() {
		if (started) {
			restartButton.setVisible(true);
		}
	}

	@Override
	public void settingsChanged() {
		log.debug("settings changed, possible track to warn of unsaved changes during close");
	}

	public void addVariables(Map<String, String> vars) {
		if (vars != null) {
			for (String key : vars.keySet()) {
				String value = vars.get(key);
				variableModel.set(key, value);
			}
		}
	}
}
