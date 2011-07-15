package com.thedeanda.ajaxproxy.ui;

import java.awt.TextField;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class GeneralPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private TextField port;
	private TextField resourceBase;

	public GeneralPanel() {
		MigLayout layout = new MigLayout("", "[right,100][600, grow]", "10[][]");
		setLayout(layout);

		port = new TextField();
		add(new JLabel("Local Port"));
		add(port, "growx, wrap");

		resourceBase = new TextField();
		add(new JLabel("Resource Base"));
		add(resourceBase, "growx, wrap");
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
}
