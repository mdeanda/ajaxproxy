package com.thedeanda.ajaxproxy.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.filter.ThrottleFilter;
import com.thedeanda.ajaxproxy.ui.options.OptionValue;
import com.thedeanda.javajson.JsonObject;

public class GeneralPanel extends JPanel implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel portLabel;
	private JTextField port;

	private JCheckBox enableSsl;
	private JCheckBox saveSslPassword;
	private JLabel sslPortLabel;
	private JTextField sslPort;
	private JLabel sslFileLabel;
	private JTextField sslFile;
	private JButton sslFileButton;
	private JLabel sslKeystorePassLabel;
	private JTextField sslKeystorePass;
	private JLabel sslKeystorePass2Label;
	private JTextField sslKeystorePass2;

	private JTextField resourceBase;
	private JCheckBox indexCheck;

	private AjaxProxy proxy;
	private JSlider forcedLatency;
	private List<OptionValue> delayOptionValues;
	private JButton folderButton;
	final JFileChooser fileChooser;
	private List<OptionValue> cacheOptionsValues;
	private JSlider cacheSlider;
	private JLabel baseLabel;
	private JLabel forcedLabel;
	private JLabel cacheLabel;

	public GeneralPanel(final SettingsChangedListener listener) {
		// TODO: track changes to text fields
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		port = SwingUtils.newJTextField();

		portLabel = new JLabel("HTTP Port");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		Dimension size = portLabel.getPreferredSize();
		size.width = 150;
		portLabel.setPreferredSize(size);

		add(portLabel);
		add(port);

		enableSsl = new JCheckBox("Enable SSL");
		saveSslPassword = new JCheckBox("Save Passwords");
		add(enableSsl);
		add(saveSslPassword);

		sslPort = SwingUtils.newJTextField();
		sslPortLabel = new JLabel("SSL Port");
		sslPortLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		sslFileLabel = new JLabel("Keyfile");
		sslFileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		sslFile = SwingUtils.newJTextField();
		sslFileButton = new JButton("...");

		add(sslPortLabel);
		add(sslPort);
		add(sslFileLabel);
		add(sslFile);
		add(sslFileButton);

		sslKeystorePassLabel = new JLabel("Keystore Password");
		sslKeystorePassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		sslKeystorePass = SwingUtils.newJTextField();
		sslKeystorePass2Label = new JLabel("Key Password");
		sslKeystorePass2Label.setHorizontalAlignment(SwingConstants.RIGHT);
		sslKeystorePass2 = SwingUtils.newJTextField();

		add(sslKeystorePassLabel);
		add(sslKeystorePass);
		add(sslKeystorePass2Label);
		add(sslKeystorePass2);

		resourceBase = SwingUtils.newJTextField();
		baseLabel = new JLabel("Resource Base");
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

		forcedLabel = new JLabel("Request Delay");
		forcedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(forcedLabel);
		add(forcedLatency);

		cacheOptionsValues = initCacheOptionValues();
		cacheSlider = createCustomSlider(cacheOptionsValues);
		cacheSlider.setToolTipText("Cacheded proxy entries will be cached for the amount of time specified here");
		cacheLabel = new JLabel("Cache Time");
		add(cacheLabel);
		add(cacheSlider);

		initLayout();
	}

	private void initLayout() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setPreferredSize(new Dimension(700, 500));

		layout.putConstraint(SpringLayout.WEST, portLabel, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, port, 5, SpringLayout.EAST, portLabel);
		layout.putConstraint(SpringLayout.NORTH, port, 60, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, port, -10, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.BASELINE, portLabel, 0, SpringLayout.BASELINE, port);

		/* ssl row 2 */
		layout.putConstraint(SpringLayout.NORTH, enableSsl, 20, SpringLayout.SOUTH, port);
		layout.putConstraint(SpringLayout.WEST, enableSsl, 0, SpringLayout.WEST, port);
		layout.putConstraint(SpringLayout.EAST, enableSsl, 150, SpringLayout.WEST, enableSsl);

		layout.putConstraint(SpringLayout.WEST, saveSslPassword, 0, SpringLayout.EAST, enableSsl);
		layout.putConstraint(SpringLayout.BASELINE, saveSslPassword, 0, SpringLayout.BASELINE, enableSsl);

		/* ssl row 2 */
		layout.putConstraint(SpringLayout.WEST, sslPortLabel, 0, SpringLayout.WEST, portLabel);
		layout.putConstraint(SpringLayout.EAST, sslPortLabel, 0, SpringLayout.EAST, portLabel);
		layout.putConstraint(SpringLayout.BASELINE, sslPortLabel, 0, SpringLayout.BASELINE, sslPort);

		layout.putConstraint(SpringLayout.NORTH, sslPort, 10, SpringLayout.SOUTH, enableSsl);
		layout.putConstraint(SpringLayout.WEST, sslPort, 0, SpringLayout.WEST, port);
		layout.putConstraint(SpringLayout.EAST, sslPort, 60, SpringLayout.WEST, sslPort);

		layout.putConstraint(SpringLayout.BASELINE, sslFileLabel, 0, SpringLayout.BASELINE, sslPort);
		layout.putConstraint(SpringLayout.WEST, sslFileLabel, 10, SpringLayout.EAST, sslPort);
		layout.putConstraint(SpringLayout.EAST, sslFileLabel, 100, SpringLayout.WEST, sslFileLabel);

		layout.putConstraint(SpringLayout.BASELINE, sslFile, 0, SpringLayout.BASELINE, sslPort);
		layout.putConstraint(SpringLayout.WEST, sslFile, 10, SpringLayout.EAST, sslFileLabel);
		layout.putConstraint(SpringLayout.EAST, sslFile, 0, SpringLayout.WEST, sslFileButton);

		layout.putConstraint(SpringLayout.BASELINE, sslFileButton, 0, SpringLayout.BASELINE, sslPort);
		layout.putConstraint(SpringLayout.EAST, sslFileButton, 0, SpringLayout.EAST, port);

		/* ssl row 3 */
		layout.putConstraint(SpringLayout.BASELINE, sslKeystorePassLabel, 0, SpringLayout.BASELINE, sslKeystorePass);
		layout.putConstraint(SpringLayout.WEST, sslKeystorePassLabel, 0, SpringLayout.WEST, portLabel);
		layout.putConstraint(SpringLayout.EAST, sslKeystorePassLabel, 0, SpringLayout.EAST, portLabel);

		layout.putConstraint(SpringLayout.NORTH, sslKeystorePass, 10, SpringLayout.SOUTH, sslPort);
		layout.putConstraint(SpringLayout.WEST, sslKeystorePass, 0, SpringLayout.WEST, sslPort);
		layout.putConstraint(SpringLayout.EAST, sslKeystorePass, 200, SpringLayout.WEST, sslKeystorePass);

		layout.putConstraint(SpringLayout.BASELINE, sslKeystorePass2Label, 0, SpringLayout.BASELINE, sslKeystorePass);
		layout.putConstraint(SpringLayout.WEST, sslKeystorePass2Label, 10, SpringLayout.EAST, sslKeystorePass);
		layout.putConstraint(SpringLayout.EAST, sslKeystorePass2Label, 100, SpringLayout.WEST, sslKeystorePass2Label);

		layout.putConstraint(SpringLayout.BASELINE, sslKeystorePass2, 0, SpringLayout.BASELINE, sslKeystorePass);
		layout.putConstraint(SpringLayout.WEST, sslKeystorePass2, 10, SpringLayout.EAST, sslKeystorePass2Label);
		layout.putConstraint(SpringLayout.EAST, sslKeystorePass2, 200, SpringLayout.WEST, sslKeystorePass2);

		/* end ssl */

		layout.putConstraint(SpringLayout.NORTH, folderButton, 40, SpringLayout.SOUTH, sslKeystorePass);
		layout.putConstraint(SpringLayout.EAST, folderButton, 0, SpringLayout.EAST, port);

		layout.putConstraint(SpringLayout.BASELINE, resourceBase, 0, SpringLayout.BASELINE, folderButton);
		layout.putConstraint(SpringLayout.WEST, resourceBase, 0, SpringLayout.WEST, port);
		layout.putConstraint(SpringLayout.EAST, resourceBase, 0, SpringLayout.WEST, folderButton);

		layout.putConstraint(SpringLayout.BASELINE, baseLabel, 0, SpringLayout.BASELINE, resourceBase);
		layout.putConstraint(SpringLayout.EAST, baseLabel, 0, SpringLayout.EAST, portLabel);

		layout.putConstraint(SpringLayout.NORTH, indexCheck, 5, SpringLayout.SOUTH, resourceBase);
		layout.putConstraint(SpringLayout.WEST, indexCheck, 0, SpringLayout.WEST, resourceBase);

		layout.putConstraint(SpringLayout.NORTH, forcedLabel, 60, SpringLayout.SOUTH, indexCheck);
		layout.putConstraint(SpringLayout.EAST, forcedLabel, 0, SpringLayout.EAST, baseLabel);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, forcedLatency, 0, SpringLayout.VERTICAL_CENTER, forcedLabel);
		layout.putConstraint(SpringLayout.WEST, forcedLatency, 5, SpringLayout.EAST, forcedLabel);
		layout.putConstraint(SpringLayout.EAST, forcedLatency, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, cacheLabel, 40, SpringLayout.SOUTH, forcedLatency);
		layout.putConstraint(SpringLayout.EAST, cacheLabel, 0, SpringLayout.EAST, baseLabel);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, cacheSlider, 0, SpringLayout.VERTICAL_CENTER, cacheLabel);
		layout.putConstraint(SpringLayout.WEST, cacheSlider, 5, SpringLayout.EAST, cacheLabel);
		layout.putConstraint(SpringLayout.EAST, cacheSlider, -10, SpringLayout.EAST, this);

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

	private List<OptionValue> initCacheOptionValues() {
		List<OptionValue> values = new ArrayList<>();

		values.add(new OptionValue("0", 0, 0));
		values.add(new OptionValue("10s", 1, 10));
		values.add(new OptionValue("30s", 2, 30));
		values.add(new OptionValue("1m", 3, 60));
		values.add(new OptionValue("5m", 4, 300));
		values.add(new OptionValue("10m", 5, 600));
		values.add(new OptionValue("60m", 6, 3600));

		return values;
	}

	private JSlider createCustomSlider(List<OptionValue> delayOptionValues) {
		int max = delayOptionValues.size() - 1;
		int major = 1;
		int minor = 1;
		JSlider ret = new JSlider();
		Dictionary<Integer, JLabel> labels = new Hashtable<>();
		for (OptionValue value : delayOptionValues) {
			labels.put(value.getSliderValue(), new JLabel(value.getLabel()));
		}

		ret.setLabelTable(labels);
		ret.setMinimum(0);
		ret.setMaximum(max);
		ret.setValue(0);
		ret.setMajorTickSpacing(major);
		ret.setMinorTickSpacing(minor);
		ret.setSnapToTicks(true);
		ret.setPaintTicks(true);
		ret.setPaintLabels(true);
		ret.addChangeListener(this);
		return ret;
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

	public void setPort(int port) {
		this.port.setText(String.valueOf(port));
	}

	public void setShowIndex(boolean showIndex) {
		indexCheck.setSelected(showIndex);
	}

	public boolean isShowIndex() {
		return indexCheck.isSelected();
	}

	public void setProxy(AjaxProxy proxy) {
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

	public void setConfig(JsonObject config) {
		if (config == null)
			return;

		setPort(config.getInt("port"));
		setResourceBase(config.getString("resourceBase"));
		setShowIndex(config.getBoolean(AjaxProxy.SHOW_INDEX));

		JsonObject options = config.getJsonObject("options");
		if (options != null) {
			forcedLatency.setValue(options.getInt("forcedLatency"));
			cacheSlider.setValue(options.getInt("cacheTime"));
		}
	}

	/** update values from current ui state into config object */
	public void updateConfig(JsonObject config) {
		config.put("port", getPort());
		config.put("resourceBase", getResourceBase());
		config.put(AjaxProxy.SHOW_INDEX, isShowIndex());

		JsonObject options = config.getJsonObject("options");
		if (options == null) {
			options = new JsonObject();
			config.put("options", options);
		}
		options.put("forcedLatency", forcedLatency.getValue());
		options.put("cacheTime", cacheSlider.getValue());
	}

	public int getCacheTime() {
		return cacheOptionsValues.get(cacheSlider.getValue()).getRealValue();
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
