package com.thedeanda.ajaxproxy.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.thedeanda.ajaxproxy.model.config.AjaxProxyConfig;
import com.thedeanda.ajaxproxy.model.config.Convertor;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;

public class AjaxProxyConfigService {
	public AjaxProxyConfig load(InputStream is) throws IOException {
		Convertor converter = Convertor.get();
		try {
			JsonObject config = JsonObject.parse(is);
			AjaxProxyConfig ajaxProxyConfig = converter.readAjaxProxyConfig(config);
			ajaxProxyConfig.setConfigFile(new File("."));
			return ajaxProxyConfig;
		} catch (JsonException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public AjaxProxyConfig load(File cf) throws IOException {
		if (!cf.exists())
			throw new FileNotFoundException("config file not found");
		try (FileInputStream fis = new FileInputStream(cf)) {
			AjaxProxyConfig ajaxProxyConfig = load(fis);
			ajaxProxyConfig.setConfigFile(cf);
			return ajaxProxyConfig;
		} finally {

		}
	}

	public void save(AjaxProxyConfig config, File output) throws IOException {

	}
}
