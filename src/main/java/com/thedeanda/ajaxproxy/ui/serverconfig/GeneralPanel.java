package com.thedeanda.ajaxproxy.ui.serverconfig;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.filter.ThrottleFilter;
import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.options.OptionValue;
import com.thedeanda.ajaxproxy.ui.util.SwingUtils;
import com.thedeanda.javajson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeneralPanel extends JPanel implements ChangeListener,
		ActionListener {
	private static final long serialVersionUID = 1L;
	private JTextField port;
	private JTextField resourceBase;
	private JCheckBox indexCheck;

	private AjaxProxyServer proxy;
	private JSlider forcedLatency;
	private List<OptionValue> delayOptionValues;
	private JButton folderButton;
	final JFileChooser fileChooser;

	public GeneralPanel(final SettingsChangedListener listener) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		// TODO: track changes to text fields
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		port = SwingUtils.newJTextField();

		JLabel portLabel = new JLabel("Local Port");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		Dimension size = portLabel.getPreferredSize();
		size.width = 150;
		portLabel.setPreferredSize(size);

		add(portLabel);
		add(port);

		resourceBase = SwingUtils.newJTextField();
		JLabel baseLabel = new JLabel("Resource Base");
		folderButton = new JButton("...");
		folderButton.addActionListener(this);

		add(baseLabel);
		add(resourceBase);
		add(folderButton);

		indexCheck = new JCheckBox("Show Directory Index");
		add(indexCheck);
		indexCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = ((JCheckBox) e.getSource()).getModel();
				if (model.isPressed()) {
					listener.restartRequired();
				}
			}
		});

		delayOptionValues = initDelayOptionValues();
		forcedLatency = createCustomSlider(delayOptionValues);
		forcedLatency
				.setToolTipText("Every request will be blocked the specified amount of time to simulate slow servers");

		JLabel forcedLabel = new JLabel("Request Delay");
		forcedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(forcedLabel);
		add(forcedLatency);

		layout.putConstraint(SpringLayout.WEST, portLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, port, 5, SpringLayout.EAST,
				portLabel);
		layout.putConstraint(SpringLayout.NORTH, port, 60, SpringLayout.NORTH,
				this);
		layout.putConstraint(SpringLayout.EAST, port, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, portLabel, 0,
				SpringLayout.VERTICAL_CENTER, port);

		layout.putConstraint(SpringLayout.NORTH, folderButton, 20,
				SpringLayout.SOUTH, port);
		layout.putConstraint(SpringLayout.EAST, folderButton, 0,
				SpringLayout.EAST, port);

		layout.putConstraint(SpringLayout.NORTH, resourceBase, 20,
				SpringLayout.SOUTH, port);
		layout.putConstraint(SpringLayout.WEST, resourceBase, 0,
				SpringLayout.WEST, port);
		layout.putConstraint(SpringLayout.EAST, resourceBase, 0,
				SpringLayout.WEST, folderButton);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, baseLabel, 0,
				SpringLayout.VERTICAL_CENTER, resourceBase);
		layout.putConstraint(SpringLayout.EAST, baseLabel, 0,
				SpringLayout.EAST, portLabel);

		layout.putConstraint(SpringLayout.NORTH, indexCheck, 20,
				SpringLayout.SOUTH, resourceBase);
		layout.putConstraint(SpringLayout.WEST, indexCheck, 0,
				SpringLayout.WEST, resourceBase);

		layout.putConstraint(SpringLayout.NORTH, forcedLabel, 60,
				SpringLayout.SOUTH, indexCheck);
		layout.putConstraint(SpringLayout.EAST, forcedLabel, 0,
				SpringLayout.EAST, baseLabel);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, forcedLatency, 0,
				SpringLayout.VERTICAL_CENTER, forcedLabel);
		layout.putConstraint(SpringLayout.WEST, forcedLatency, 5,
				SpringLayout.EAST, forcedLabel);
		layout.putConstraint(SpringLayout.EAST, forcedLatency, -10,
				SpringLayout.EAST, this);

		Dimension panelSize = new Dimension(500, 300);
		this.setPreferredSize(panelSize);
		this.setMinimumSize(panelSize);
	}

	private List<OptionValue> initDelayOptionValues() {
		List<OptionValue> values = new ArrayList<>();

		values.add(new OptionValue("0", 0, 0));
		values.add(new OptionValue("100ms", 1, 100));
		values.add(new OptionValue("250ms", 2, 250));
		values.add(new OptionValue("500ms", 3, 500));
		values.add(new OptionValue("1s", 4, 1000));
		values.add(new OptionValue("2s", 5, 2000));
		values.add(new OptionValue("5s", 6, 5000));
		values.add(new OptionValue("10s", 7, 10000));
		values.add(new OptionValue("30s", 8, 30000));

		return values;
	}

	private JSlider createCustomSlider(List<OptionValue> delayOptionValues) {
		JSlider slider = SwingUtils.createCustomSlider(delayOptionValues);
		slider.addChangeListener(this);
		return slider;
	}

	public String getResourceBase() {
		return resourceBase.getText();
	}

	public int getPort() {
		return Integer.parseInt(port.getText());
	}

	public void setResourceBase(String rb) {
		resourceBase.setText(rb);
	}

	public void setPort(String port) {
		this.port.setText(port);
	}

	public void setShowIndex(boolean showIndex) {
		indexCheck.setSelected(showIndex);
	}

	public boolean isShowIndex() {
		return indexCheck.isSelected();
	}

	public void setProxy(AjaxProxyServer proxy) {
		this.proxy = proxy;
		this.applyOptions();
	}

	@Override
	public void stateChanged(ChangeEvent evt) {
		JSlider source = (JSlider) evt.getSource();
		if (!source.getValueIsAdjusting()) {
			applyOptions();
		}
	}

	private void applyOptions() {
		if (proxy == null)
			return;

		ThrottleFilter filter = proxy.getThrottleFilter();
		if (filter == null)
			return;

		int value = forcedLatency.getValue();
		OptionValue optionValue = delayOptionValues.get(value);
		int latency = 0;
		if (optionValue != null) {
			latency = optionValue.getRealValue();
		}
		filter.setForcedLatency(latency);
		// filter.setMaxBitrate(maxBitrate.getValue());
	}

	public void setConfig(ServerConfig serverConfig) {
		if (serverConfig == null)
			return;

		setPort(serverConfig.getPort().getOriginalValue());
		setResourceBase(serverConfig.getResourceBase().getOriginalValue());
		setShowIndex(serverConfig.isShowIndex());

		//TODO: map to ui values instead
		forcedLatency.setValue(serverConfig.getForcedLatencyMs());
	}

	/** update values from current ui state into config object */
	public void updateConfig(JsonObject config) {
		config.put("port", getPort());
		config.put("resourceBase", getResourceBase());
		config.put(AjaxProxyServer.SHOW_INDEX, isShowIndex());

		JsonObject options = config.getJsonObject("options");
		if (options == null) {
			options = new JsonObject();
			config.put("options", options);
		}
		options.put("forcedLatency", forcedLatency.getValue());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == folderButton) {
			pickResourceBase();
		}
	}

	private void pickResourceBase() {
		if (!StringUtils.isBlank(resourceBase.getText())) {
			// TODO: have a method to resolve relative path
			File file = new File(resourceBase.getText());
			if (file.exists()) {
				fileChooser.setCurrentDirectory(file);
			}
		}
		int retVal = fileChooser.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// TODO: convert to relative path when possible
			resourceBase.setText(file.getAbsolutePath());
		}
	}
}
