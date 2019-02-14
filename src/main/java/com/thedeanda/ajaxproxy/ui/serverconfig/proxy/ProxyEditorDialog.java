package com.thedeanda.ajaxproxy.ui.serverconfig.proxy;

import java.awt.Component;

import javax.swing.*;

import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;

public class ProxyEditorDialog {

	private static ProxyConfig showDialog(EditorPanel panel, ProxyConfig value, String title, Component parent) {
		panel.setValue(value);

		String[] options = new String[] { "OK", "Cancel" };
		int option = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(parent), panel, title, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);

		ProxyConfig result = null;
		if (option == 0) {
			result = panel.getResult();
		}

		return result;
	}

	public static ProxyConfig showEditDialog(ProxyConfigRequest config, Component parent) {
		if (config != null) {
			RequestProxyEditorPanel panel = new RequestProxyEditorPanel();
			ProxyConfig result = showDialog(panel, config, "Edit Proxy", parent);

			return result;
		} else
			return null;
	}

	public static ProxyConfig showEditDialog(ProxyConfigFile config, Component parent) {
		if (config != null) {
			FileProxyEditorPanel panel = new FileProxyEditorPanel();
			ProxyConfig result = showDialog(panel, config, "Edit Proxy", parent);

			return result;
		} else
			return null;
	}

	public static ProxyConfig showAddProxyDialog(Component parent) {
		RequestProxyEditorPanel panel = new RequestProxyEditorPanel();
		ProxyConfig result = showDialog(panel, null, "Add Proxy", parent);

		return result;
	}

	public static ProxyConfig showAddFileDialog(Component parent) {
		FileProxyEditorPanel panel = new FileProxyEditorPanel();
		ProxyConfig result = showDialog(panel, null, "Add Path", parent);

		return result;
	}
}
