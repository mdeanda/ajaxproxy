package com.thedeanda.ajaxproxy.ui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigService {
	private static final Logger log = LoggerFactory
			.getLogger(ConfigService.class);
	private static ConfigService instance = new ConfigService();
	private File configDir;

	private ConfigService() {
		configDir = new File(System.getProperty("user.home") + File.separator
				+ ".ajaxproxy");
		try {
			migrateV1toV2();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
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
}
