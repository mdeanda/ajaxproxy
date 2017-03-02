package com.thedeanda.ajaxproxy.ui.proxy;

import java.awt.Component;

import javax.swing.JDialog;

import com.thedeanda.ajaxproxy.model.config.ProxyConfig;

public class ProxyEditorDialog {
	private static ProxyConfig showDialog(ProxyConfig value, String title, Component parent) {
		ProxyEditorPanel panel = new ProxyEditorPanel();
		final JDialog frame = new JDialog();
		frame.setModal(true);
		frame.setTitle(title);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);
		
		value = panel.getResult();
		
		return value;
	}

	public static ProxyConfig showEditDialog(ProxyConfig config, Component parent) {
		ProxyConfig result = showDialog(config, "Edit Proxy", parent);

		return result;
	}
}
