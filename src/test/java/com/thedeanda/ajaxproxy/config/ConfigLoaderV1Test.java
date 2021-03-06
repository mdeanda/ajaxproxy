package com.thedeanda.ajaxproxy.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.MergeConfig;
import com.thedeanda.ajaxproxy.config.model.MergeMode;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.Variable;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
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
		assertThat(config).isNull();

		jsonConfig = null;
		config = loader.loadConfig(jsonConfig, workingDir);
		assertThat(config).isNull();

		jsonConfig = new JsonObject();
		config = loader.loadConfig(jsonConfig, null);
		assertThat(config).isNull();
	}

	@Test
	public void v1() throws Exception {
		jsonConfig = JsonLoader.load("/com.thedeanda.ajaxproxy.config/v1.json");
		config = loader.loadConfig(jsonConfig, workingDir);

		validate();
	}

	@Test
	public void v1vars() throws Exception {
		jsonConfig = JsonLoader.load("/com.thedeanda.ajaxproxy.config/v1.vars.json");
		config = loader.loadConfig(jsonConfig, workingDir);

		validate();
	}

	private void validate() {
		assertThat(config).isNotNull();
		assertThat(config.getVariables()).isNotNull();
		assertThat(config.getVariables()).hasSize(3);
		assertThat(config.getVariables()).contains(Variable.builder().key("folder").value("32").build());
		assertThat(config.getVariables()).contains(Variable.builder().key("host").value("typicode").build());
		assertThat(config.getVariables()).contains(Variable.builder().key("port").value("8080").build());
		assertThat(config.getWorkingDir()).isEqualTo(new File(".").getAbsolutePath());

		assertThat(config.getServers()).hasSize(1);
		ServerConfig server = config.getServers().get(0);
		assertThat(server.getPort()).isNotNull();
		assertThat(server.getPort().getValue()).isEqualTo(8080);
		assertThat(server.getResourceBase()).isNotNull();
		assertThat(server.getResourceBase().getValue()).isEqualTo("./32");
		assertThat(server.isShowIndex()).isEqualTo(true);
		assertThat(server.getCacheTimeSec()).isEqualTo(30);
		assertThat(server.getForcedLatencyMs()).isEqualTo(500);

		assertThat(server.getMergeConfig()).hasSize(2);

		MergeConfig merge = server.getMergeConfig().get(0);
		assertThat(merge).isNotNull();
		assertThat(merge.getFilePath().getValue()).isEqualTo("donkey");
		assertThat(merge.getPath().getValue()).isEqualTo("/monkey/32/");
		assertThat(merge.getMode()).isEqualTo(MergeMode.CSS);
		assertThat(merge.isMinify()).isTrue();

		merge = server.getMergeConfig().get(1);
		assertThat(merge).isNotNull();
		assertThat(merge.getFilePath().getValue()).isEqualTo("/home/mdeanda/32/file");
		assertThat(merge.getPath().getValue()).isEqualTo("/balloon");
		assertThat(merge.getMode()).isEqualTo(MergeMode.JS);
		assertThat(merge.isMinify()).isFalse();

		List<ProxyConfig> proxies = server.getProxyConfig();
		assertThat(proxies).isNotNull().hasSize(2);

		ProxyConfig proxy = proxies.get(0);
		ProxyConfigRequest proxyR = (ProxyConfigRequest) proxy;
		assertThat(proxyR.isEnableCache()).isFalse();
		assertThat(proxyR.getCacheDuration()).isEqualTo(30);
		assertThat(proxyR.getHost().getValue()).isEqualTo("jsonplaceholder.typicode.com");
		assertThat(proxyR.getPath().getValue()).isEqualTo("/users/.*");
		assertThat(proxyR.getPort()).isEqualTo(80);
		assertThat(proxyR.getProtocol()).isEqualTo("http");

		proxy = proxies.get(1);
		proxyR = (ProxyConfigRequest) proxy;
		assertThat(proxyR.isEnableCache()).isFalse();
		assertThat(proxyR.getCacheDuration()).isEqualTo(30);
		assertThat(proxyR.getHost().getValue()).isEqualTo("example.com");
		assertThat(proxyR.getPath().getValue()).isEqualTo("/32/.*");
		assertThat(proxyR.getPort()).isEqualTo(80);
		assertThat(proxyR.getProtocol()).isEqualTo("https");
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

		assertThat(config).isNull();
	}

}
