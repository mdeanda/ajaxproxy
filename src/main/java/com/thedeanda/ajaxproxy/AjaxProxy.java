package com.thedeanda.ajaxproxy;

import com.thedeanda.ajaxproxy.config.ConfigLoader;
import com.thedeanda.ajaxproxy.config.ConfigService;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.MergeMode;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
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
import java.util.*;
import java.util.regex.Pattern;

public class AjaxProxy implements Runnable, LoggerMessageListener {
	private static final Logger log = LoggerFactory.getLogger(AjaxProxy.class);
	// TODO: move to config
	private String keystoreFile = "";
	private String keystorePassword = "";

	private String resourceBase = "";
	private JsonObject config;
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

	private final ConfigService configService;

	private enum ProxyEvent {
		START, STOP, FAIL
	};

	public static final String PORT = "port";
	public static final String RESOURCE_BASE = "resourceBase";
	public static final String SHOW_INDEX = "showIndex";
	public static final String PROXY_ARRAY = "proxy";
	public static final String DOMAIN = "domain";
	public static final String PATH = "path";
	public static final String MERGE_ARRAY = "merge";
	public static final String FILE_PATH = "filePath";
	private static final String MODE = "mode";
	private static final String MINIFY = "minify";

	public AjaxProxy(JsonObject config, File workingDir, RequestListener listener, ConfigService configService) throws Exception {
		this.configService = configService;
		init(config, workingDir, listener);
	}

	public AjaxProxy(String configFile, ConfigService configService) throws Exception {
		this.configService = configService;
		log.info("using config file: " + configFile);
		File cf = new File(configFile);
		if (!cf.exists())
			throw new FileNotFoundException("config file not found");
		File configDir = cf.getParentFile();
		if (configDir == null)
			configDir = new File(".");
		try (FileInputStream fis = new FileInputStream(cf)) {
			config = JsonObject.parse(fis);
		}
		init(config, configDir, new EmptyRequestListener());
	}

	private void init(JsonObject config, File workingDir, RequestListener listener) {
		this.listener = listener;

		// TODO: perhaps pass in config object
		configObject = configService.loadConfig(config, workingDir);

		this.config = config;
		this.workingDir = workingDir;

		throttleFilter = new ThrottleFilter();

		// TODO: one filter per server?
		ServerConfig firstServer = configObject.getServers().get(0);
		proxyFilter = new ProxyFilter(this, firstServer);
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

	private void doVar(String variable, String value, JsonValue target) {
		if (target.isJsonArray()) {
			for (JsonValue next : target.getJsonArray()) {
				doVar(variable, value, next);
			}
		} else if (target.isJsonObject()) {
			JsonObject json = target.getJsonObject();
			for (String key : json) {
				doVar(variable, value, json.get(key));
			}
		} else if (target.isString()) {
			String s = target.getString();
			String v = "${" + variable + "}";
			if (s != null && s.indexOf(v) >= 0) {
				s = s.replaceAll(Pattern.quote(v), value);
				target.setString(s);
			}
		}
	}

	private void doVars() {
		JsonObject vars = config.getJsonObject("variables");
		if (vars != null) {
			JsonArray proxy = config.getJsonArray("proxy");
			if (proxy != null)
				doVars(vars, proxy);

			JsonArray merge = config.getJsonArray("merge");
			if (merge != null)
				doVars(vars, merge);
		}
	}

	private void doVars(JsonObject vars, JsonArray target) {
		for (String val : vars) {
			if (vars.isString(val) || vars.isInt(val)) {
				for (JsonValue targetValue : target) {
					doVar(val, vars.getString(val), targetValue);
				}
			}
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

	private void initRun() throws Exception {
		doVars();

		if (config.isString(RESOURCE_BASE)) {
			String rb = config.getString(RESOURCE_BASE);
			resourceBase = workingDir.getPath() + File.separator + rb;
			File tmp = new File(resourceBase);
			if (!tmp.exists()) {
				tmp = new File(rb);
			}
			if (!tmp.exists()) {
				throw new FileNotFoundException("Resource base not found: " + rb);
			}
			resourceBase = tmp.getCanonicalPath();
		} else {
			throw new Exception("resourceBase not defined in config file");
		}
		log.info("using resource base: " + resourceBase);
	}

	/** returns the list of proxy paths (after resolving variables) */
	public List<ProxyPath> getProxyPaths() {
		List<ProxyPath> ret = new ArrayList<ProxyPath>();
		if (config.isJsonArray(PROXY_ARRAY)) {
			JsonArray pa = config.getJsonArray(PROXY_ARRAY);
			for (JsonValue val : pa) {
				int port = 80;
				String domain = null;
				String path = null;
				JsonObject obj = val.getJsonObject();
				if (obj.isString(DOMAIN))
					domain = obj.getString(DOMAIN);
				if (obj.isString(PATH))
					path = obj.getString(PATH);
				if (obj.isInt(PORT))
					port = obj.getInt(PORT);
				else if (obj.isString(PORT))
					port = Integer.parseInt(obj.getString(PORT));

				if (domain != null && path != null && port > 0) {
					ProxyPath proxyPath = ProxyPath.builder().domain(domain).port(port).path(path).build();
					ret.add(proxyPath);
				}
			}
		}
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
		log.info("starting jetty server");
		try {
			fireEvent(ProxyEvent.START);
			initRun();

			// TODO: for now assume just 1 server in config, later we'll loop through all
			ServerConfig serverConfig = configObject.getServers().get(0);
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
			servlet.setInitParameter("resourceBase", resourceBase);
			servlet.setInitParameter("maxCacheSize", "0");
			servlet.setName("default servlet");
			root.addServlet(servlet, "/");

			if (config.isJsonArray(MERGE_ARRAY)) {
				JsonArray a = config.getJsonArray(MERGE_ARRAY);
				for (JsonValue val : a) {
					if (val.isJsonObject()) {
						JsonObject obj = val.getJsonObject();
						String path = obj.getString(PATH);
						String filePath = obj.getString(FILE_PATH);
						File fPath = new File(resourceBase + File.separator + filePath);
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
						log.debug("{}", obj);
						boolean minify = obj.getBoolean(MINIFY);
						MergeMode mode = obj.hasKey(MODE) ? MergeMode.valueOf(obj.getString(MODE)) : MergeMode.PLAIN;
						log.debug("adding merge servlet: " + path + " to merge " + filePath);
						MergeServlet ms = new MergeServlet(filePath, mode, minify, path);
						mergeServlets.add(ms);
						root.addServlet(new ServletHolder(ms), path);
					}
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

				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							log.info("Shutting down jetty");
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

	public JsonObject getConfig() {
		return config;
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
