package com.thedeanda.ajaxproxy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JCheckBox;
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
	private JCheckBox logRequests;
	private JTextField appendToPath;
	private JSlider maxBitrate;
	private AjaxProxy proxy;
	private JSlider forcedLatency;
	private JCheckBox logInput;
	private JCheckBox logCookies;
	private JCheckBox logOutput;
	private JLabel logExpressionLabel;
	private JTextField logExpression;
	private JLabel contentExpressionLabel;
	private JTextField contentExpression;

	public OptionsPanel() {
		MigLayout layout = new MigLayout("", "[right,150][300, grow]20",
				"[]15[]15[]10[]0[][]");
		setLayout(layout);

		maxBitrate = createSlider(50, 10, 1);
		appendToPath = new JTextField();
		forcedLatency = createSlider(500, 100, 10);
		logRequests = new JCheckBox("Log Requests");
		logInput = new JCheckBox("Log Input");
		logCookies = new JCheckBox("Log Cookies");
		logOutput = new JCheckBox("Log Output");
		logExpressionLabel = new JLabel("URL RegExp");
		logExpression = new JTextField();
		contentExpressionLabel = new JLabel("Content RegExp");
		contentExpression = new JTextField();

		appendToPath.addPropertyChangeListener(this);
		logRequests.addActionListener(this);
		logInput.addActionListener(this);
		logOutput.addActionListener(this);
		logCookies.addActionListener(this);
		logExpression.addPropertyChangeListener(this);
		contentExpression.addPropertyChangeListener(this);

		add(new JLabel("Forced Latency (ms)"));
		add(forcedLatency, "growx, wrap");

		add(new JLabel("Max Bitrate (KBps)"));
		add(maxBitrate, "growx, wrap");

		add(new JLabel("Append to Path"));
		add(appendToPath, "growx, wrap");

		add(logRequests, "skip, wrap");
		add(logInput, "skip, wrap");
		add(logCookies, "skip, wrap");
		add(logOutput, "skip, wrap");

		add(logExpressionLabel);
		add(logExpression, "growx, wrap");

		add(contentExpressionLabel);
		add(contentExpression, "growx, wrap");
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
		boolean enable = logRequests.isSelected();
		logInput.setEnabled(enable);
		logOutput.setEnabled(enable);
		logCookies.setEnabled(enable);
		logExpressionLabel.setEnabled(enable);
		logExpression.setEnabled(enable);
		contentExpressionLabel.setEnabled(enable);
		contentExpression.setEnabled(enable);

		Pattern urlPattern = null;
		try {
			String exp = logExpression.getText();
			if (exp != null && !"".equals(exp.trim()))
				urlPattern = Pattern.compile(exp);
		} catch (PatternSyntaxException pse) {
			logExpression.setText("");
			urlPattern = null;
		}

		Pattern contentPattern = null;
		try {
			String exp = contentExpression.getText();
			if (exp != null && !"".equals(exp.trim()))
				contentPattern = Pattern.compile(exp, Pattern.MULTILINE
						| Pattern.DOTALL);
		} catch (PatternSyntaxException pse) {
			contentExpression.setText("");
			contentPattern = null;
		}

		if (proxy == null)
			return;

		APFilter filter = proxy.getApfilter();
		if (filter == null)
			return;

		filter.setAppendToPath(appendToPath.getText());
		filter.setForcedLatency(forcedLatency.getValue());
		filter.setLogRequests(logRequests.isSelected());
		filter.setMaxBitrate(maxBitrate.getValue());
		filter.setLogCookies(logCookies.isSelected());
		filter.setLogInput(logInput.isSelected());
		filter.setLogOutput(logOutput.isSelected());
		filter.setLogExpression(urlPattern);
		filter.setContentExpression(contentPattern);

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
