package com.thedeanda.ajaxproxy;

import com.thedeanda.ajaxproxy.config.ConfigLoader;
import com.thedeanda.ajaxproxy.config.ConfigService;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.MergeConfig;
import com.thedeanda.ajaxproxy.config.model.MergeMode;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.filter.ProxyFilter;
import com.thedeanda.ajaxproxy.filter.ThrottleFilter;
import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessage;
import com.thedeanda.ajaxproxy.filter.handler.logger.LoggerMessageListener;
import com.thedeanda.ajaxproxy.http.EmptyRequestListener;
import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.model.ProxyPath;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AllowSymLinkAliasChecker;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.server.ExportException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AjaxProxy implements Runnable, LoggerMessageListener {
	private static final Logger log = LoggerFactory.getLogger(AjaxProxy.class);
	// TODO: move to config
	private String keystoreFile = "";
	private String keystorePassword = "";

	private final int id;
	private Server jettyServer;
	private File workingDir;
	private List<ProxyListener> listeners = new ArrayList<ProxyListener>();
	private ProxyFilter proxyFilter;
	private ThrottleFilter throttleFilter;
	private boolean mergeMode = false;
	private List<MergeServlet> mergeServlets = new ArrayList<MergeServlet>();
	private Collection<LoggerMessageListener> messageListeners = new HashSet<>();
	private RequestListener listener;

	private Config configObject;
	private ServerConfig serverConfig;

	private enum ProxyEvent {
		START, STOP, FAIL
	};

	public static final String PORT = "port";
	public static final String RESOURCE_BASE = "resourceBase";
	public static final String SHOW_INDEX = "showIndex";
	public static final String PROXY_ARRAY = "proxy";
	public static final String DOMAIN = "domain";
	public static final String PATH = "path";

	private static AtomicInteger instanceId = new AtomicInteger(0);

	public AjaxProxy(JsonObject config, File workingDir, RequestListener listener, ConfigService configService) throws Exception {
		id = instanceId.incrementAndGet();

		// TODO: pass in config object
		configObject = configService.loadConfig(config, workingDir);
		ServerConfig firstServer = configObject.getServers().get(0);

		init(config, workingDir, listener, firstServer);
	}

	public AjaxProxy(String configFile, ConfigService configService) throws Exception {
		id = instanceId.incrementAndGet();

		log.info("using config file: " + configFile);
		JsonObject config;
		File cf = new File(configFile);
		if (!cf.exists())
			throw new FileNotFoundException("config file not found");
		File configDir = cf.getParentFile();
		if (configDir == null)
			configDir = new File(".");
		try (FileInputStream fis = new FileInputStream(cf)) {
			config = JsonObject.parse(fis);
		}

		// TODO: pass in config object
		configObject = configService.loadConfig(config, workingDir);
		ServerConfig firstServer = configObject.getServers().get(0);
		init(config, configDir, new EmptyRequestListener(), firstServer);
	}

	private void init(JsonObject config, File workingDir, RequestListener listener, ServerConfig serverConfig) {
		this.listener = listener;

		this.serverConfig = serverConfig;
		this.workingDir = workingDir;

		throttleFilter = new ThrottleFilter();

		proxyFilter = new ProxyFilter(this, serverConfig);
		getRequestListener();
	}

	public void addProxyListener(ProxyListener pl) {
		synchronized (listeners) {
			listeners.add(pl);
		}
	}

	public void addLoggerMessageListener(LoggerMessageListener listener) {
		synchronized (messageListeners) {
			this.messageListeners.add(listener);
		}
	}

	private void fireEvent(ProxyEvent evt) {
		ArrayList<ProxyListener> lst = new ArrayList<ProxyListener>();
		synchronized (listeners) {
			lst.addAll(listeners);
		}
		for (ProxyListener pl : lst) {
			switch (evt) {
			case START:
				pl.started();
				break;
			case STOP:
				pl.stopped();
				break;
			case FAIL:
				pl.failed();
				break;
			}
		}
	}

	private String getResourceBase() throws Exception {
		String rb = configObject.getWorkingDir() + File.separator + serverConfig.getResourceBase().getValue();
		File tmp = new File(rb);
		if (!tmp.exists()) {
			tmp = new File(serverConfig.getResourceBase().getValue());
		}
		if (!tmp.exists()) {
			throw new FileNotFoundException("Resource base not found: " + rb);
		}
		String resourceBase = tmp.getCanonicalPath();
		log.info("{} using resource base: {}", id, resourceBase);
		return resourceBase;
	}

	/** returns the list of proxy paths (after resolving variables) */
	public List<ProxyPath> getProxyPaths() {
		//TODO: not sure if we did the same for other types
		List<ProxyPath> ret = serverConfig.getProxyConfig().stream()
				.filter(ProxyConfigRequest.class::isInstance)
				.map(ProxyConfigRequest.class::cast)
				.map(proxyConfigRequest ->
						ProxyPath.builder()
								.domain(proxyConfigRequest.getHost().getValue())
								.port(proxyConfigRequest.getPort())
								.path(proxyConfigRequest.getPath().getValue())
								.build()
				)
				.collect(Collectors.toList());

		return ret;
	}

	private void initConnectors(Server jettyServer, ServerConfig serverConfig) {
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		int httpsPort = serverConfig.getHttpsPort().getValue();
		if (httpsPort > 0) {
			http_config.setSecurePort(httpsPort);
		}
		http_config.setOutputBufferSize(32768);
		http_config.setRequestHeaderSize(8192);
		http_config.setResponseHeaderSize(8192);
		http_config.setSendServerVersion(true);
		http_config.setSendDateHeader(false);

		int port = serverConfig.getPort().getValue();
		if (port > 0) {
			ServerConnector http = new ServerConnector(jettyServer, new HttpConnectionFactory(http_config));
			http.setPort(port);
			http.setIdleTimeout(30000);
			jettyServer.addConnector(http);
		}
		if (httpsPort > 0 && !StringUtils.isBlank(keystoreFile)) {
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(keystoreFile);

			if (!StringUtils.isBlank(keystorePassword)) {
				sslContextFactory.setKeyStorePassword(keystorePassword);
			}
			// sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
			// sslContextFactory.setTrustStorePath(jetty_home +
			// "/../../../jetty-server/src/test/config/etc/keystore");
			// sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
			sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA",
					"SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
					"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
					"SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

			HttpConfiguration https_config = new HttpConfiguration(http_config);
			https_config.addCustomizer(new SecureRequestCustomizer());

			ServerConnector sslConnector = new ServerConnector(jettyServer,
					new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
					new HttpConnectionFactory(https_config));
			sslConnector.setPort(httpsPort);
			jettyServer.addConnector(sslConnector);
		}
	}

	public void run() {
		log.info("{} starting jetty server: {}", id, serverConfig);
		try {
			fireEvent(ProxyEvent.START);
			boolean showIndex = serverConfig.isShowIndex();

			jettyServer = new Server();
			initConnectors(jettyServer, serverConfig);

			ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
			root.setContextPath("/");
			jettyServer.setHandler(root);

			AllowSymLinkAliasChecker alias = new AllowSymLinkAliasChecker();
			root.addAliasCheck(alias);

			FilterHolder throttleFilterHolder = new FilterHolder(throttleFilter);
			EnumSet<DispatcherType> dispatches = EnumSet.allOf(DispatcherType.class);
			root.addFilter(throttleFilterHolder, "/*", dispatches);

			FilterHolder proxyFilterHolder = new FilterHolder(proxyFilter);
			root.addFilter(proxyFilterHolder, "/*", dispatches);
			proxyFilter.reset();

			ServletHolder servlet;
			DefaultServlet defaultServlet = new DefaultServlet();
			servlet = new ServletHolder(defaultServlet);
			servlet.setInitParameter("dirAllowed", String.valueOf(showIndex));
			servlet.setInitParameter("resourceBase", getResourceBase());
			servlet.setInitParameter("maxCacheSize", "0");
			servlet.setName("default servlet");
			root.addServlet(servlet, "/");

			if (CollectionUtils.isNotEmpty(serverConfig.getMergeConfig())) {
				for (MergeConfig mergeConfig : serverConfig.getMergeConfig()) {
					String path = mergeConfig.getPath().getValue();
					String filePath = mergeConfig.getFilePath().getValue();
					File fPath = new File(getResourceBase() + File.separator + filePath);
					if (!fPath.exists()) {
						log.warn("file not found: " + fPath.getCanonicalPath());
						// TODO: this throws exception if filePath is null
						// and its hard to identify
						fPath = new File(filePath);
					}
					if (!fPath.exists()) {
						throw new FileNotFoundException(fPath.getAbsolutePath());
					}
					filePath = fPath.getCanonicalPath();
					log.debug("{}", mergeConfig);
					boolean minify = mergeConfig.isMinify();
					MergeMode mode = mergeConfig.getMode();
					log.debug("adding merge servlet: " + path + " to merge " + filePath);
					MergeServlet ms = new MergeServlet(filePath, mode, minify, path);
					mergeServlets.add(ms);
					root.addServlet(new ServletHolder(ms), path);
				}
			}

			if (!mergeMode) {
				try {
					jettyServer.start();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					jettyServer.stop();
					throw e;
				}

				//TODO: remove shutdown hook when manually stopped
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							log.info("{} shutting down jetty", id);
							jettyServer.stop();
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				});
			}
		} catch (Exception e) {
			fireEvent(ProxyEvent.FAIL);
			log.error(e.getMessage(), e);
		}
	}

	// TODO: needs to reset or add a reset to clear all listener lists to avoid
	// memory leak from ui
	public void stop() {
		try {
			if (jettyServer != null) {
				jettyServer.stop();
				fireEvent(ProxyEvent.STOP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<MergeServlet> getMergeServlets() {
		return mergeServlets;
	}

	public void setMergeMode(boolean mergeMode) {
		this.mergeMode = mergeMode;
	}

	public RequestListener getRequestListener() {
		return listener;
	}

	public ThrottleFilter getThrottleFilter() {
		return throttleFilter;
	}

	@Override
	public void messageReceived(LoggerMessage message) {
		Collection<LoggerMessageListener> tmp = new HashSet<>();
		synchronized (messageListeners) {
			tmp.addAll(messageListeners);
		}

		for (LoggerMessageListener l : tmp) {
			try {
				l.messageReceived(message);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
	}

}
