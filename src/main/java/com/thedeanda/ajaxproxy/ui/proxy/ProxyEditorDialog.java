package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.thedeanda.ajaxproxy.model.config.ProxyConfig;

public class ProxyEditorDialog {

	private static ProxyConfig showDialog(ProxyConfig value, String title, Component parent) {
		ProxyEditorPanel panel = new ProxyEditorPanel();
		panel.setValue(value);

		String[] options = new String[] { "OK", "Cancel" };
		int option = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);

		ProxyConfig result = null;
		if (option == 0) {
			result = panel.getResult();
		}

		return result;
	}

	public static ProxyConfig showEditDialog(ProxyConfig config, Component parent) {
		ProxyConfig result = showDialog(config, "Edit Proxy", parent);

		return result;
	}

	public static ProxyConfig showAddDialog(ProxyConfig config, Component parent) {
		ProxyConfig result = showDialog(config, "Add Proxy", parent);

		return result;
	}
}
