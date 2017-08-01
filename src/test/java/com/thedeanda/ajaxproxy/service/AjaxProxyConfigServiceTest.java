package com.thedeanda.ajaxproxy.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.thedeanda.ajaxproxy.model.config.AjaxProxyConfig;

public class AjaxProxyConfigServiceTest {
	private AjaxProxyConfigService service;
	
	@Before
	public void init() {
		service = new AjaxProxyConfigService();
	}
	
	@Test
	public void testLoadConfig() throws IOException {
		InputStream is = getClass().getResourceAsStream("/sample_config.json");
		
		assertNotNull(is);
		AjaxProxyConfig config = service.load(is);
		assertNotNull(config);
		
		assertEquals(8080, config.getPort());
		assertEquals("/var/www", config.getResourceBase());
		assertEquals(new File("."), config.getConfigFile());
	}
}
