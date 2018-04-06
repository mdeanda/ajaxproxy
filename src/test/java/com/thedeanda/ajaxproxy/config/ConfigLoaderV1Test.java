package com.thedeanda.ajaxproxy.config;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.javajson.JsonObject;

public class ConfigLoaderV1Test {

	private ConfigLoaderV1 loader;

	private JsonObject jsonConfig;

	private File workingDir;

	private Config config;

	@Before
	public void init() {
		loader = new ConfigLoaderV1();
		jsonConfig = null;
		workingDir = new File(".");
		config = null;
	}

	@Test
	public void testEmpty() {
		jsonConfig = new JsonObject();
		config = loader.loadConfig(jsonConfig, workingDir);
		assertNull(config);

		jsonConfig = null;
		config = loader.loadConfig(jsonConfig, workingDir);
		assertNull(config);

		jsonConfig = new JsonObject();
		config = loader.loadConfig(jsonConfig, null);
		assertNull(config);
	}

	@Test
	public void v1() throws Exception {
		jsonConfig = JsonLoader.load("/com.thedeanda.ajaxproxy.config/v1.json");
		config = loader.loadConfig(jsonConfig, workingDir);

		assertNotNull(config);
		assertNotNull(config.getVariables());
		assertThat(config.getVariables().size(), is(1));
		assertThat(config.getVariables().get(0).getKey(), is("folder"));
		assertThat(config.getVariables().get(0).getValue(), is("32"));
		assertThat(config.getWorkingDir(), is("."));
		assertThat(config.getServers(), is(not(null)));
		assertFalse(config.getServers().isEmpty());
	}

	/**
	 * v2 files shouldn't create a config object
	 * 
	 * @throws Exception
	 */
	@Test
	public void v2() throws Exception {
		jsonConfig = JsonLoader.load("/com.thedeanda.ajaxproxy.config/v2.json");
		config = loader.loadConfig(jsonConfig, workingDir);

		assertNull(config);
	}

}
