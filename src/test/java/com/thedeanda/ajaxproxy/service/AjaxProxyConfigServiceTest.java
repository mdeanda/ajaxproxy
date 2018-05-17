package com.thedeanda.ajaxproxy.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
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
		assertTrue(config.isShowIndex());

		
		Map<String, String> vars = config.getVariables();
		assertNotNull(vars);
		assertEquals(2, vars.size());
		assertTrue(vars.containsKey("host"));
		assertTrue(vars.containsKey("path"));
		assertEquals("jsonplaceholder.typicode.com", vars.get("host"));
		assertEquals("/users", vars.get("path"));
		
		List<ProxyConfig> proxies = config.getProxyConfig();
		assertNotNull(proxies);
		assertEquals(3, proxies.size()); //"logger" is auto-added (for now)
		
		//TOOD: add a way to disable logger and change path
		ProxyConfigLogger logger = (ProxyConfigLogger) proxies.get(0);
		assertNotNull(logger);
		
		ProxyConfigRequest first = (ProxyConfigRequest) proxies.get(1);
		assertNotNull(first);
		assertEquals("${host}", first.getHost());
		assertEquals("${path}/.*", first.getPath());
		assertEquals(80, first.getPort());
		assertEquals(10, first.getCacheDuration());
		assertTrue(first.isEnableCache());
		
		ProxyConfigRequest second = (ProxyConfigRequest) proxies.get(2);
		assertNotNull(second);
		assertEquals("example.com", second.getHost());
		assertEquals("/example/.*", second.getPath());
		assertEquals(8080, second.getPort());
		assertEquals(20, second.getCacheDuration());
		assertFalse(second.isEnableCache());
	}
}

