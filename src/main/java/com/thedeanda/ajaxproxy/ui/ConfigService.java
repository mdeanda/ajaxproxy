package com.thedeanda.ajaxproxy.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigService {
	private static final Logger log = LoggerFactory
			.getLogger(ConfigService.class);
	private static ConfigService instance = new ConfigService();
	private File configDir;
	private String version = null;

	private ConfigService() {
		configDir = new File(System.getProperty("user.home") + File.separator
				+ ".ajaxproxy");
		try {
			migrateV1toV2();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		loadVersionString();
	}

	public static ConfigService get() {
		return instance;
	}

	private void migrateV1toV2() throws IOException {
		log.debug("migrateV1toV2");
		File oldConfig = new File(System.getProperty("user.home")
				+ File.separator + ".ajaxproxy");
		if (oldConfig.isFile()) {
			log.debug("old file exists");
			// load file into memory, delete, create folder, save into folder
			// with new name
			byte[] bytes = FileUtils.readFileToByteArray(oldConfig);

			oldConfig.delete();
			configDir.mkdirs();
			FileUtils.writeByteArrayToFile(getConfigFile(), bytes);
		}
	}

	private void loadVersionString() {
		InputStream is = getClass().getResourceAsStream("/version.properties");
		if (is != null) {
			Properties props = new Properties();
			try {
				props.load(is);
			} catch (IOException ex) {
				log.warn(ex.getMessage(), ex);
			}
			String versionString = props.getProperty("version");
			version = StringUtils.trimToEmpty(versionString);
		}
	}

	public String getVersionString() {
		return version;
	}

	public File getConfigFile() {
		String recentFilePath = "config.js";
		File f = new File(configDir, recentFilePath);
		return f;
	}

	public File getRestHistoryDb() {
		String recentFilePath = "rest.db";
		File f = new File(configDir, recentFilePath);
		return f;
	}

	public File getResourceHistoryDb() {
		String recentFilePath = "resource.db";
		File f = new File(configDir, recentFilePath);
		return f;
	}
}
