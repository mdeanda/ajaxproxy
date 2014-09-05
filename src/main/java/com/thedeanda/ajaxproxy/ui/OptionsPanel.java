package com.thedeanda.ajaxproxy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.filter.APFilter;

public class OptionsPanel extends JPanel implements ActionListener,
		PropertyChangeListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private JTextField appendToPath;
	private JSlider maxBitrate;
	private AjaxProxy proxy;
	private JSlider forcedLatency;

	private static final int COL_WIDTH = 200;

	public OptionsPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		maxBitrate = createSlider(100, 10, 5);
		appendToPath = SwingUtils.newJTextField();
		forcedLatency = createSlider(500, 100, 10);

		appendToPath.addPropertyChangeListener(this);

		JLabel forcedLabel = new JLabel("Forced Latency (ms)");
		forcedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(forcedLabel);
		add(forcedLatency);

		JLabel maxBwLabel = new JLabel("Max Bitrate (KBps)");
		maxBwLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(maxBwLabel);
		add(maxBitrate);

		JLabel appendLabel = new JLabel("Append to Path");
		appendLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		add(appendLabel);
		add(appendToPath);

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

		layout.putConstraint(SpringLayout.NORTH, appendToPath, 20,
				SpringLayout.SOUTH, maxBitrate);
		layout.putConstraint(SpringLayout.EAST, appendToPath, 0,
				SpringLayout.EAST, maxBitrate);
		layout.putConstraint(SpringLayout.WEST, appendToPath, 0,
				SpringLayout.WEST, maxBitrate);

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

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, appendLabel, 0,
				SpringLayout.VERTICAL_CENTER, appendToPath);
		layout.putConstraint(SpringLayout.WEST, appendLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, appendLabel, -10,
				SpringLayout.WEST, appendToPath);

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

		APFilter filter = proxy.getApfilter();
		if (filter == null)
			return;

		filter.setAppendToPath(appendToPath.getText());
		filter.setForcedLatency(forcedLatency.getValue());
		filter.setMaxBitrate(maxBitrate.getValue());
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

}
