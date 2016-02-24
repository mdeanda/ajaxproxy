package com.thedeanda.ajaxproxy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.filter.ThrottleFilter;
import com.thedeanda.ajaxproxy.ui.options.OptionValue;
import com.thedeanda.javajson.JsonObject;

public class OptionsPanel extends JPanel implements ActionListener,
		PropertyChangeListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private JSlider maxBitrate;
	private AjaxProxy proxy;
	private JSlider forcedLatency;
	private List<OptionValue> delayOptionValues;

	private static final int COL_WIDTH = 200;

	public OptionsPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		delayOptionValues = initDelayOptionValues();

		maxBitrate = createSlider(100, 10, 5);
		maxBitrate.setEnabled(false);
		forcedLatency = createDelaySlider(delayOptionValues);
		forcedLatency
				.setToolTipText("Every request will be blocked the specified amount of time to simulate slow servers");

		JLabel forcedLabel = new JLabel("Forced Latency");
		forcedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(forcedLabel);
		add(forcedLatency);

		JLabel maxBwLabel = new JLabel("Max Bitrate (KBps)");
		maxBwLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(maxBwLabel);
		add(maxBitrate);

		layout.putConstraint(SpringLayout.NORTH, forcedLatency, 60,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, forcedLatency, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, forcedLatency, COL_WIDTH,
				SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.NORTH, maxBitrate, 20,
				SpringLayout.SOUTH, forcedLatency);
		layout.putConstraint(SpringLayout.EAST, maxBitrate, 0,
				SpringLayout.EAST, forcedLatency);
		layout.putConstraint(SpringLayout.WEST, maxBitrate, 0,
				SpringLayout.WEST, forcedLatency);

		// labels

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, forcedLabel, 0,
				SpringLayout.VERTICAL_CENTER, forcedLatency);
		layout.putConstraint(SpringLayout.WEST, forcedLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, forcedLabel, -10,
				SpringLayout.WEST, forcedLatency);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, maxBwLabel, 0,
				SpringLayout.VERTICAL_CENTER, maxBitrate);
		layout.putConstraint(SpringLayout.WEST, maxBwLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, maxBwLabel, -10,
				SpringLayout.WEST, maxBitrate);

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

		return values;
	}

	private JSlider createDelaySlider(List<OptionValue> delayOptionValues) {
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

	private JSlider createSlider(int max, int major, int minor) {
		JSlider ret = new JSlider();
		ret.setMinimum(0);
		ret.setMaximum(max);
		ret.setValue(0);
		ret.setMajorTickSpacing(major);
		// if (minor > 1)
		ret.setMinorTickSpacing(minor);
		// if (minor > 1)
		ret.setSnapToTicks(true);
		ret.setPaintTicks(true);
		ret.setPaintLabels(true);
		ret.addChangeListener(this);
		return ret;
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

	public void setProxy(AjaxProxy proxy) {
		this.proxy = proxy;
		this.applyOptions();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		applyOptions();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		applyOptions();
	}

	@Override
	public void stateChanged(ChangeEvent evt) {
		JSlider source = (JSlider) evt.getSource();
		if (!source.getValueIsAdjusting()) {
			applyOptions();
		}
	}

	public JsonObject getConfig() {
		JsonObject data = new JsonObject();
		data.put("maxBitrate", maxBitrate.getValue());
		data.put("forcedLatency", forcedLatency.getValue());
		return data;
	}

	public void setConfig(JsonObject config) {
		if (config == null)
			return;
		maxBitrate.setValue(config.getInt("maxBitrate"));
		forcedLatency.setValue(config.getInt("forcedLatency"));
	}
}
