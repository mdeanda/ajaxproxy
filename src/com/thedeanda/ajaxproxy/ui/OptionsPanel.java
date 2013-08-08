package com.thedeanda.ajaxproxy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.thedeanda.ajaxproxy.APFilter;
import com.thedeanda.ajaxproxy.AjaxProxy;

public class OptionsPanel extends JPanel implements ActionListener,
		PropertyChangeListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private JTextField appendToPath;
	private JSlider maxBitrate;
	private AjaxProxy proxy;
	private JSlider forcedLatency;

	public OptionsPanel() {
		MigLayout layout = new MigLayout("", "[right,150][300, grow]20",
				"[]15[]15[]10[]0[][]");
		setLayout(layout);

		maxBitrate = createSlider(100, 10, 5);
		appendToPath = new JTextField();
		forcedLatency = createSlider(500, 100, 10);

		appendToPath.addPropertyChangeListener(this);

		add(new JLabel("Forced Latency (ms)"));
		add(forcedLatency, "growx, wrap");

		add(new JLabel("Max Bitrate (KBps)"));
		add(maxBitrate, "growx, wrap");

		add(new JLabel("Append to Path"));
		add(appendToPath, "growx, wrap");
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
